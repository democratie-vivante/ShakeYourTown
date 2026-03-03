package io.mbras.syt.missions.storage

import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.MissionTheme
import io.mbras.syt.missions.storage.MissionStorage.Result
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant

class MissionStorage(private val dataDir: File) {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val file = File(dataDir, "missions.json")

    private var missions: MutableList<Mission> = mutableListOf()

    init {
        load()
    }

    private fun load() {
        if (file.exists()) {
            try {
                val content = file.readText()
                if (content.isNotBlank()) {
                    missions = json.decodeFromString<MutableList<Mission>>(content)
                }
            } catch (e: Exception) {
                missions = mutableListOf()
            }
        }
    }

    private fun save() {
        dataDir.mkdirs()
        file.writeText(json.encodeToString(missions))
    }

    fun getAll(): List<Mission> = missions.toList()

    fun getPublished(theme: MissionTheme? = null, upcomingOnly: Boolean = false): List<Mission> {
        return missions.filter { mission ->
            val isPublic = mission.status in listOf(MissionStatus.PUBLISHED, MissionStatus.FULL, MissionStatus.DONE)
            val themeMatch = theme == null || mission.theme == theme
            val upcomingMatch = !upcomingOnly || mission.dateTime > Instant.now().toString()
            isPublic && themeMatch && upcomingMatch
        }.sortedBy { it.dateTime }
    }

    fun getArchive(theme: MissionTheme? = null, period: String? = null): List<Mission> {
        return missions.filter { mission ->
            val isDone = mission.status == MissionStatus.DONE
            val themeMatch = theme == null || mission.theme == theme
            isDone && themeMatch
        }.sortedByDescending { it.dateTime }
    }

    fun getById(id: String): Mission? = missions.find { it.id == id }

    fun getByOrganizer(organizerId: String): List<Mission> = missions.filter { it.organizerId == organizerId }

    fun create(mission: Mission): Result {
        val now = Instant.now().toString()
        val newMission = mission.copy(
            createdAt = now,
            updatedAt = now
        )
        missions.add(newMission)
        save()
        return Result.Success(newMission)
    }

    fun update(mission: Mission): Result {
        val index = missions.indexOfFirst { it.id == mission.id }
        return if (index >= 0) {
            val updated = mission.copy(updatedAt = Instant.now().toString())
            missions[index] = updated
            save()
            Result.Success(updated)
        } else {
            Result.NotFound
        }
    }

    fun updateStatus(id: String, status: MissionStatus): Result {
        val mission = getById(id) ?: return Result.NotFound
        val updated = mission.copy(
            status = status,
            updatedAt = Instant.now().toString()
        )
        return update(updated)
    }

    fun incrementParticipants(id: String): Result {
        val mission = getById(id) ?: return Result.NotFound
        if (mission.currentParticipants >= mission.maxParticipants) {
            return Result.Error("Mission is full")
        }
        val updated = mission.copy(
            currentParticipants = mission.currentParticipants + 1,
            updatedAt = Instant.now().toString(),
            status = if (mission.currentParticipants + 1 >= mission.maxParticipants) MissionStatus.FULL else mission.status
        )
        return update(updated)
    }

    fun decrementParticipants(id: String): Result {
        val mission = getById(id) ?: return Result.NotFound
        val updated = mission.copy(
            currentParticipants = maxOf(0, mission.currentParticipants - 1),
            updatedAt = Instant.now().toString(),
            status = if (mission.status == MissionStatus.FULL && mission.currentParticipants - 1 < mission.maxParticipants) MissionStatus.PUBLISHED else mission.status
        )
        return update(updated)
    }

    sealed class Result {
        data class Success(val mission: Mission) : Result()
        data object NotFound : Result()
        data class Error(val message: String) : Result()
    }
}
