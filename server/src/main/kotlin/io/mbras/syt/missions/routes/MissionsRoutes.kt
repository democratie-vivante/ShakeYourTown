package io.mbras.syt.missions.routes

import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionTheme
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.Signup
import io.mbras.syt.missions.storage.MissionStorage
import io.mbras.syt.missions.storage.SignupStorage
import io.mbras.syt.missions.storage.OrganizerStorage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Routing.missionsRoutes(
    missionStorage: MissionStorage,
    signupStorage: SignupStorage,
    organizerStorage: OrganizerStorage
) {
    route("/api/v1/missions") {
        get {
            val theme = call.parameters["theme"]?.let {
                try { MissionTheme.valueOf(it.uppercase()) } catch (e: Exception) { null }
            }
            val upcomingOnly = call.parameters["upcoming"]?.toBoolean() ?: false
            val missions = missionStorage.getPublished(theme, upcomingOnly)
            call.respond(MissionListResponse(missions.map { it.toPublicDto() }))
        }

        get("/archive") {
            val theme = call.parameters["theme"]?.let {
                try { MissionTheme.valueOf(it.uppercase()) } catch (e: Exception) { null }
            }
            val period = call.parameters["period"]
            val missions = missionStorage.getArchive(theme, period)
            call.respond(MissionListResponse(missions.map { it.toArchiveDto() }))
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            if (mission == null) {
                call.respondError(404, "Mission introuvable")
            } else if (mission.status !in listOf(MissionStatus.PUBLISHED, MissionStatus.FULL, MissionStatus.DONE)) {
                call.respondError(404, "Mission introuvable")
            } else {
                call.respond(mission.toDetailDto(organizerStorage))
            }
        }

        post("/{id}/signup") {
            val id = call.parameters["id"] ?: return@post call.respondError(400, "Identifiant de mission manquant")
            val mission = missionStorage.getById(id)
            
            if (mission == null) {
                return@post call.respondError(404, "Mission introuvable")
            }
            
            if (mission.status !in listOf(MissionStatus.PUBLISHED, MissionStatus.FULL)) {
                return@post call.respondError(409, "Cette mission n'est pas disponible pour inscription")
            }

            val request = call.receive<SignupRequest>()
            
            if (request.contactEmail.isBlank() && request.contactPhone.isBlank()) {
                return@post call.respondError(400, "Un email ou un num\u00e9ro de t\u00e9l\u00e9phone est requis")
            }

            if (signupStorage.existsByMissionAndContact(id, request.contactEmail.takeIf { it.isNotBlank() }, request.contactPhone.takeIf { it.isNotBlank() })) {
                return@post call.respondError(409, "Vous \u00eates d\u00e9j\u00e0 inscrit(e) \u00e0 cette mission")
            }

            val signup = Signup(
                missionId = id,
                participantName = request.participantName,
                contactEmail = request.contactEmail,
                contactPhone = request.contactPhone
            )
            
            val signupResult = missionStorage.tryCreateSignup(id)
            when (signupResult) {
                is MissionStorage.Result.NotFound -> return@post call.respondError(404, "Mission introuvable")
                is MissionStorage.Result.Error -> return@post call.respondError(409, signupResult.message)
                is MissionStorage.Result.Success -> {
                    signupStorage.create(signup)
                }
            }

            call.respond(
                status = io.ktor.http.HttpStatusCode.Created,
                message = SignupResponse(
                    success = true,
                    signupId = signup.id,
                    message = "Inscription confirm\u00e9e ! Une confirmation a \u00e9t\u00e9 envoy\u00e9e \u00e0 ${request.contactEmail.ifBlank { request.contactPhone }}"
                )
            )
        }
    }
}

@Serializable
data class MissionListResponse(val missions: List<Map<String, String>>)

@Serializable
data class SignupResponse(val success: Boolean, val signupId: String, val message: String)

@Serializable
data class MissionApiError(val error: String, val message: String)

private fun Mission.toPublicDto() = mapOf(
    "id" to id,
    "title" to title,
    "description" to description,
    "theme" to theme.name,
    "dateTime" to dateTime,
    "location" to location,
    "maxParticipants" to maxParticipants.toString(),
    "currentParticipants" to currentParticipants.toString(),
    "status" to status.name,
    "whatToBring" to whatToBring
)

private fun Mission.toDetailDto(organizerStorage: OrganizerStorage): Map<String, String> {
    val dto = toPublicDto().toMutableMap()
    val organizer = organizerStorage.getById(organizerId)
    dto["organizerContact"] = organizer?.organization?.let { "$it contact" } ?: "mission@town.fr"
    return dto
}

private fun Mission.toArchiveDto() = mapOf(
    "id" to id,
    "title" to title,
    "theme" to theme.name,
    "dateTime" to dateTime,
    "status" to status.name,
    "actualParticipants" to actualParticipants.toString(),
    "completionSummary" to completionSummary
)

@Serializable
data class SignupRequest(
    val participantName: String,
    val contactEmail: String = "",
    val contactPhone: String = ""
)

private suspend fun ApplicationCall.respondError(code: Int, message: String) {
    val errorCode = when (code) {
        400 -> "VALIDATION_ERROR"
        404 -> "NOT_FOUND"
        409 -> "MISSION_ERROR"
        else -> "MISSION_ERROR"
    }
    respond(
        status = io.ktor.http.HttpStatusCode.fromValue(code),
        message = MissionApiError(errorCode, message)
    )
}
