package io.mbras.syt.missions.auth

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthMiddleware(private val sessionManager: SessionManager) {

    fun authenticate(call: ApplicationCall): String? {
        val token = call.request.cookies["session"] ?: call.request.headers["Authorization"]?.removePrefix("Bearer ")
        return token?.let { sessionManager.getOrganizerId(it) }
    }

    fun requireAuth(call: ApplicationCall): String {
        val organizerId = authenticate(call)
            ?: throw AuthException("Authentication required")
        return organizerId
    }

    class AuthException(message: String) : Exception(message)
}
