package io.mbras.syt.missions.routes

import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionTheme
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.Signup
import io.mbras.syt.missions.storage.MissionStorage
import io.mbras.syt.missions.storage.SignupStorage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Routing.missionsRoutes(
    missionStorage: MissionStorage,
    signupStorage: SignupStorage
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
            val id = call.parameters["id"] ?: return@get call.respondError(400, "Missing mission ID")
            val mission = missionStorage.getById(id)
            if (mission == null) {
                call.respondError(404, "Mission not found")
            } else if (mission.status !in listOf(MissionStatus.PUBLISHED, MissionStatus.FULL, MissionStatus.DONE)) {
                call.respondError(404, "Mission not found")
            } else {
                call.respond(mission.toDetailDto())
            }
        }

        post("/{id}/signup") {
            val id = call.parameters["id"] ?: return@post call.respondError(400, "Missing mission ID")
            val mission = missionStorage.getById(id)
            
            if (mission == null) {
                return@post call.respondError(404, "Mission not found")
            }
            
            if (mission.status !in listOf(MissionStatus.PUBLISHED, MissionStatus.FULL)) {
                return@post call.respondError(409, "Mission is not available for signup")
            }

            val request = call.receive<SignupRequest>()
            
            if (request.contactEmail.isBlank() && request.contactPhone.isBlank()) {
                return@post call.respondError(400, "Email or phone required")
            }

            if (signupStorage.existsByMissionAndContact(id, request.contactEmail.takeIf { it.isNotBlank() }, request.contactPhone.takeIf { it.isNotBlank() })) {
                return@post call.respondError(409, "Already signed up for this mission")
            }

            val signup = Signup(
                missionId = id,
                participantName = request.participantName,
                contactEmail = request.contactEmail,
                contactPhone = request.contactPhone
            )
            
            signupStorage.create(signup)
            missionStorage.incrementParticipants(id)

            call.respond(
                status = io.ktor.http.HttpStatusCode.Created,
                message = SignupResponse(
                    success = true,
                    signupId = signup.id,
                    message = "You're signed up! Confirmation sent to ${request.contactEmail.ifBlank { request.contactPhone }}"
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

private fun Mission.toDetailDto(): Map<String, String> {
    val dto = toPublicDto().toMutableMap()
    dto["organizerContact"] = "mission@town.fr"
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
    val errorCode = if (code == 400) "VALIDATION_ERROR" else "MISSION_ERROR"
    respond(
        status = io.ktor.http.HttpStatusCode.fromValue(code),
        message = MissionApiError(errorCode, message)
    )
}
