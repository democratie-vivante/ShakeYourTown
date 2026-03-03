package io.mbras.syt.missions.routes

import io.mbras.syt.missions.auth.SessionManager
import io.mbras.syt.missions.storage.OrganizerStorage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Routing.authRoutes(
    sessionManager: SessionManager,
    organizerStorage: OrganizerStorage
) {
    route("/api/v1/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val token = sessionManager.createSession(request.username, request.password)
            
            if (token == null) {
                call.respond(
                    status = io.ktor.http.HttpStatusCode.Unauthorized,
                    message = LoginErrorResponse(
                        error = "INVALID_CREDENTIALS",
                        message = "Invalid username or password"
                    )
                )
                return@post
            }

            val organizer = organizerStorage.getByUsername(request.username)
            call.response.cookies.apply {
                append(
                    name = "session",
                    value = token,
                    httpOnly = true,
                    secure = true,
                    maxAge = 8 * 60 * 60L
                )
            }
            call.respond(LoginSuccessResponse(
                success = true,
                organizer = LoginOrganizer(
                    id = organizer?.id ?: "",
                    name = organizer?.name ?: "",
                    organization = organizer?.organization ?: ""
                )
            ))
        }

        post("/logout") {
            val token = call.request.cookies["session"]
            if (token != null) {
                sessionManager.invalidateSession(token)
            }
            call.response.cookies.apply {
                append(name = "session", value = "", maxAge = 0)
            }
            call.respond(LoginSuccessResponse(success = true, organizer = null))
        }
    }
}

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginSuccessResponse(
    val success: Boolean,
    val organizer: LoginOrganizer?
)

@Serializable
data class LoginOrganizer(
    val id: String,
    val name: String,
    val organization: String
)

@Serializable
data class LoginErrorResponse(
    val error: String,
    val message: String
)
