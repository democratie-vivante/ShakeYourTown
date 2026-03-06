package com.shakeyourtown.missions.storage

import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.MissionTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Local CRUD storage for missions on the organizer's device.
 * Persists to PlatformStorage (localStorage on WASM, in-memory on Android/iOS).
 */
object MissionStorage {
    private const val KEY = "syt_missions"
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun loadAll(): List<Mission> {
        val raw = PlatformStorage.getString(KEY) ?: return emptyList()
        return try {
            json.decodeFromString<List<Mission>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveAll(missions: List<Mission>) {
        PlatformStorage.putString(KEY, json.encodeToString(missions))
    }

    fun getById(id: String): Mission? = loadAll().find { it.id == id }

    fun getPublished(theme: MissionTheme? = null): List<Mission> {
        return loadAll()
            .filter { it.status == MissionStatus.PUBLISHED || it.status == MissionStatus.FULL }
            .let { list ->
                if (theme != null) list.filter { it.theme == theme } else list
            }
            .sortedBy { it.dateTime }
    }

    fun getArchive(): List<Mission> {
        return loadAll()
            .filter { it.status == MissionStatus.DONE }
            .sortedByDescending { it.dateTime }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun create(
        title: String,
        description: String,
        theme: MissionTheme,
        dateTime: String,
        location: String,
        maxParticipants: Int,
        whatToBring: String = ""
    ): Mission {
        val now = kotlinx.datetime.Clock.System.now().toString()
        val mission = Mission(
            id = Uuid.random().toString(),
            title = title,
            description = description,
            theme = theme,
            dateTime = dateTime,
            location = location,
            maxParticipants = maxParticipants,
            whatToBring = whatToBring,
            organizerId = "local",
            status = MissionStatus.DRAFT,
            currentParticipants = 0,
            createdAt = now,
            updatedAt = now
        )
        val missions = loadAll().toMutableList()
        missions.add(mission)
        saveAll(missions)
        return mission
    }

    fun update(id: String, transform: (Mission) -> Mission): Mission? {
        val missions = loadAll().toMutableList()
        val index = missions.indexOfFirst { it.id == id }
        if (index < 0) return null
        val now = kotlinx.datetime.Clock.System.now().toString()
        val updated = transform(missions[index]).copy(updatedAt = now)
        missions[index] = updated
        saveAll(missions)
        return updated
    }

    fun updateStatus(id: String, newStatus: MissionStatus): Mission? {
        return update(id) { it.copy(status = newStatus) }
    }

    fun delete(id: String): Boolean {
        val missions = loadAll().toMutableList()
        val removed = missions.removeAll { it.id == id }
        if (removed) saveAll(missions)
        return removed
    }

    /**
     * Returns only PUBLISHED and FULL missions as JSON for the public static file.
     * This is what gets pushed to GitHub Pages.
     */
    fun exportPublicJson(): String {
        val publicMissions = loadAll()
            .filter { it.status in listOf(MissionStatus.PUBLISHED, MissionStatus.FULL, MissionStatus.DONE) }
            .sortedBy { it.dateTime }
        return json.encodeToString(publicMissions)
    }
}
