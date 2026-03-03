package io.mbras.syt.missions.storage

import com.shakeyourtown.missions.models.Organizer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.time.Instant

class OrganizerStorage(private val dataDir: File) {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val file = File(dataDir, "organizers.json")

    private var organizers: MutableList<Organizer> = mutableListOf()

    init {
        load()
        ensureDefaultOrganizer()
    }

    private fun load() {
        if (file.exists()) {
            try {
                val content = file.readText()
                if (content.isNotBlank()) {
                    organizers = json.decodeFromString<MutableList<Organizer>>(content)
                }
            } catch (e: Exception) {
                organizers = mutableListOf()
            }
        }
    }

    private fun save() {
        dataDir.mkdirs()
        file.writeText(json.encodeToString(organizers))
    }

    private fun ensureDefaultOrganizer() {
        if (organizers.isEmpty()) {
            val defaultOrganizer = Organizer(
                username = "admin",
                passwordHash = hashPassword("changeme123"),
                name = "Admin User",
                organization = "Town Hall",
                createdAt = Instant.now().toString()
            )
            organizers.add(defaultOrganizer)
            save()
        }
    }

    fun getAll(): List<Organizer> = organizers.toList()

    fun getById(id: String): Organizer? = organizers.find { it.id == id }

    fun getByUsername(username: String): Organizer? = organizers.find { it.username == username }

    fun create(organizer: Organizer): Organizer {
        val newOrganizer = organizer.copy(
            createdAt = Instant.now().toString()
        )
        organizers.add(newOrganizer)
        save()
        return newOrganizer
    }

    fun updateLastLogin(id: String) {
        val index = organizers.indexOfFirst { it.id == id }
        if (index >= 0) {
            organizers[index] = organizers[index].copy(lastLoginAt = Instant.now().toString())
            save()
        }
    }

    fun verifyPassword(username: String, password: String): Organizer? {
        val organizer = getByUsername(username) ?: return null
        val hashedInput = hashPassword(password)
        return if (organizer.passwordHash == hashedInput) organizer else null
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
