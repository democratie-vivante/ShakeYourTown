package io.mbras.syt.missions.routes

import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.MissionTheme
import io.mbras.syt.missions.storage.MissionStorage
import io.mbras.syt.missions.storage.SignupStorage
import io.mbras.syt.missions.storage.OrganizerStorage
import io.mbras.syt.missions.auth.SessionManager
import io.mbras.syt.missions.auth.AuthMiddleware
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Routing.organizerRoutes(
    missionStorage: MissionStorage,
    signupStorage: SignupStorage,
    organizerStorage: OrganizerStorage,
    sessionManager: SessionManager
) {
    val authMiddleware = AuthMiddleware(sessionManager)

    route("/api/v1/organizer/missions") {
        get {
            val organizerId = authMiddleware.requireAuth(call)
            val missions = missionStorage.getByOrganizer(organizerId)
            call.respond(OrganizerMissionListResponse(missions.map { mapOf(
                "id" to it.id,
                "title" to it.title,
                "status" to it.status.name,
                "currentParticipants" to it.currentParticipants.toString(),
                "dateTime" to it.dateTime
            ) }))
        }

        post {
            val organizerId = authMiddleware.requireAuth(call)
            val request = call.receive<CreateMissionRequest>()
            val mission = Mission(
                title = request.title,
                description = request.description,
                theme = try { MissionTheme.valueOf(request.theme.uppercase()) } catch (e: Exception) { MissionTheme.RESOURCES },
                dateTime = request.dateTime,
                location = request.location,
                maxParticipants = request.maxParticipants,
                whatToBring = request.whatToBring ?: "",
                organizerId = organizerId
            )
            val result = missionStorage.create(mission)
            when (result) {
                is MissionStorage.Result.Success -> {
                    call.response.status(io.ktor.http.HttpStatusCode.Created)
                    call.respond(MissionCreateResponse(result.mission.id, result.mission.status.name))
                }
                is MissionStorage.Result.NotFound -> call.respondError(404, "\u00c9chec de la cr\u00e9ation de la mission")
                is MissionStorage.Result.Error -> call.respondError(400, result.message)
            }
        }

        get("/{id}") {
            val organizerId = authMiddleware.requireAuth(call)
            val id = call.parameters["id"] ?: return@get call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            
            if (mission == null || mission.organizerId != organizerId) {
                return@get call.respondError(404, "Mission introuvable")
            }
            call.respond(mission)
        }

        put("/{id}") {
            val organizerId = authMiddleware.requireAuth(call)
            val id = call.parameters["id"] ?: return@put call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            
            if (mission == null || mission.organizerId != organizerId) {
                return@put call.respondError(404, "Mission introuvable")
            }

            val request = call.receive<UpdateMissionRequest>()
            val updated = mission.copy(
                title = request.title ?: mission.title,
                description = request.description ?: mission.description,
                theme = request.theme?.let { try { MissionTheme.valueOf(it.uppercase()) } catch (e: Exception) { mission.theme } } ?: mission.theme,
                dateTime = request.dateTime ?: mission.dateTime,
                location = request.location ?: mission.location,
                maxParticipants = request.maxParticipants ?: mission.maxParticipants,
                whatToBring = request.whatToBring ?: mission.whatToBring
            )
            
            val result = missionStorage.update(updated)
            when (result) {
                is MissionStorage.Result.Success -> call.respond(MissionCreateResponse(result.mission.id, result.mission.status.name))
                else -> call.respondError(400, "\u00c9chec de la mise \u00e0 jour de la mission")
            }
        }

        patch("/{id}/status") {
            val organizerId = authMiddleware.requireAuth(call)
            val id = call.parameters["id"] ?: return@patch call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            
            if (mission == null || mission.organizerId != organizerId) {
                return@patch call.respondError(404, "Mission introuvable")
            }

            val request = call.receive<StatusUpdateRequest>()
            val newStatus = try { MissionStatus.valueOf(request.status.uppercase()) } catch (e: Exception) {
                return@patch call.respondError(400, "Statut invalide")
            }

            if (!isValidTransition(mission.status, newStatus)) {
                return@patch call.respondError(400, "Transition de statut non autoris\u00e9e de ${mission.status} vers $newStatus")
            }

            val result = missionStorage.updateStatus(id, newStatus)
            when (result) {
                is MissionStorage.Result.Success -> call.respond(MissionCreateResponse(result.mission.id, result.mission.status.name))
                else -> call.respondError(400, "\u00c9chec de la mise \u00e0 jour du statut")
            }
        }

        get("/{id}/signups") {
            val organizerId = authMiddleware.requireAuth(call)
            val id = call.parameters["id"] ?: return@get call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            
            if (mission == null || mission.organizerId != organizerId) {
                return@get call.respondError(404, "Mission introuvable")
            }

            val signups = signupStorage.getByMissionForExport(id)
            call.respond(SignupListResponse(signups.map { mapOf(
                "id" to it.id,
                "participantName" to it.participantName,
                "contactEmail" to it.contactEmail,
                "contactPhone" to it.contactPhone,
                "signedUpAt" to it.signedUpAt,
                "status" to it.status.name
            ) }))
        }

        get("/{id}/export") {
            val organizerId = authMiddleware.requireAuth(call)
            val id = call.parameters["id"] ?: return@get call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            
            if (mission == null || mission.organizerId != organizerId) {
                return@get call.respondError(404, "Mission introuvable")
            }

            val signups = signupStorage.getByMissionForExport(id)
            val csv = buildString {
                appendLine("Nom du participant,Email,T\u00e9l\u00e9phone,Inscrit le,Statut")
                signups.forEach {
                    appendLine("${escapeCsv(it.participantName)},${escapeCsv(it.contactEmail)},${escapeCsv(it.contactPhone)},${escapeCsv(it.signedUpAt)},${escapeCsv(it.status.name)}")
                }
            }

            call.response.headers.append(io.ktor.http.HttpHeaders.ContentDisposition, "attachment; filename=\"mission-${id}-signups.csv\"")
            call.respondText(csv, io.ktor.http.ContentType.Text.CSV)
        }
    }
}

private fun escapeCsv(value: String): String {
    return if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.startsWith("=") || value.startsWith("@") || value.startsWith("+") || value.startsWith("-")) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }
}

private fun isValidTransition(from: MissionStatus, to: MissionStatus): Boolean {
    return when (from) {
        MissionStatus.DRAFT -> to == MissionStatus.PUBLISHED
        MissionStatus.PUBLISHED -> to in listOf(MissionStatus.CANCELLED, MissionStatus.DONE)
        MissionStatus.FULL -> to in listOf(MissionStatus.CANCELLED, MissionStatus.DONE)
        MissionStatus.CANCELLED -> false
        MissionStatus.DONE -> false
    }
}

@Serializable
data class OrganizerMissionListResponse(val missions: List<Map<String, String>>)

@Serializable
data class MissionCreateResponse(val id: String, val status: String)

@Serializable
data class SignupListResponse(val signups: List<Map<String, String>>)

@Serializable
data class CreateMissionRequest(
    val title: String,
    val description: String,
    val theme: String,
    val dateTime: String,
    val location: String,
    val maxParticipants: Int,
    val whatToBring: String? = null
)

@Serializable
data class UpdateMissionRequest(
    val title: String? = null,
    val description: String? = null,
    val theme: String? = null,
    val dateTime: String? = null,
    val location: String? = null,
    val maxParticipants: Int? = null,
    val whatToBring: String? = null
)

@Serializable
data class StatusUpdateRequest(
    val status: String
)

@Serializable
data class OrganizerApiError(val error: String, val message: String)

private suspend fun ApplicationCall.respondError(code: Int, message: String) {
    respond(
        status = io.ktor.http.HttpStatusCode.fromValue(code),
        message = OrganizerApiError("MISSION_ERROR", message)
    )
}
