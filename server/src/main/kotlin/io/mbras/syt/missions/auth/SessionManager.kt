package io.mbras.syt.missions.auth

import io.mbras.syt.missions.storage.OrganizerStorage
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SessionManager(private val organizerStorage: OrganizerStorage) {

    private val sessions = ConcurrentHashMap<String, Session>()

    data class Session(
        val token: String,
        val organizerId: String,
        val createdAt: Long = System.currentTimeMillis()
    )

    fun createSession(username: String, password: String): String? {
        val organizer = organizerStorage.verifyPassword(username, password) ?: return null
        val token = UUID.randomUUID().toString()
        sessions[token] = Session(token, organizer.id)
        organizerStorage.updateLastLogin(organizer.id)
        return token
    }

    fun validateSession(token: String): Session? {
        val session = sessions[token] ?: return null
        val maxAge = 8 * 60 * 60 * 1000 // 8 hours
        return if (System.currentTimeMillis() - session.createdAt < maxAge) session else {
            sessions.remove(token)
            null
        }
    }

    fun getOrganizerId(token: String): String? = validateSession(token)?.organizerId

    fun invalidateSession(token: String) {
        sessions.remove(token)
    }
}
