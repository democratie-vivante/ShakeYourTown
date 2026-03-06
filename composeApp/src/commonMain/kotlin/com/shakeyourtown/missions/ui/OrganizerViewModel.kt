package com.shakeyourtown.missions.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shakeyourtown.missions.github.GitHubPublisher
import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.MissionTheme
import com.shakeyourtown.missions.storage.MissionStorage
import com.shakeyourtown.missions.storage.SettingsStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the organizer side.
 * All data is stored locally (PlatformStorage).
 * Publishing pushes a static JSON file to GitHub Pages.
 */
class OrganizerViewModel {
    var isAuthenticated by mutableStateOf(false)
        private set
    var organizerName by mutableStateOf("")
        private set
    var organizerOrganization by mutableStateOf("")
        private set

    var missions by mutableStateOf<List<OrganizerMission>>(emptyList())
        private set
    var selectedMission by mutableStateOf<OrganizerMission?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var pinError by mutableStateOf<String?>(null)
        private set

    // Publishing state
    var isPublishing by mutableStateOf(false)
        private set
    var publishSuccess by mutableStateOf<String?>(null)
        private set
    var publishError by mutableStateOf<String?>(null)
        private set

    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Check if a PIN is already set. If not, organizer needs to set one up.
     */
    fun isPinSet(): Boolean = SettingsStorage.isPinSet()

    /**
     * Verify the PIN entered by the organizer.
     */
    fun verifyPin(pin: String, onSuccess: () -> Unit = {}) {
        if (SettingsStorage.verifyPin(pin)) {
            isAuthenticated = true
            val settings = SettingsStorage.load()
            organizerName = settings.organizerName
            organizerOrganization = settings.organizerOrganization
            pinError = null
            onSuccess()
        } else {
            pinError = "Code PIN incorrect"
        }
    }

    /**
     * Set a new PIN (first-time setup or change).
     */
    fun setupPin(pin: String, name: String, organization: String, onSuccess: () -> Unit = {}) {
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            pinError = "Le code PIN doit contenir 4 chiffres"
            return
        }
        SettingsStorage.setPin(pin)
        val settings = SettingsStorage.load()
        SettingsStorage.save(settings.copy(organizerName = name, organizerOrganization = organization))
        isAuthenticated = true
        organizerName = name
        organizerOrganization = organization
        pinError = null
        onSuccess()
    }

    fun logout(onComplete: () -> Unit = {}) {
        isAuthenticated = false
        organizerName = ""
        organizerOrganization = ""
        missions = emptyList()
        selectedMission = null
        onComplete()
    }

    /**
     * Load all missions from local storage.
     */
    fun loadMissions() {
        val allMissions = MissionStorage.loadAll()
        missions = allMissions.map { it.toOrganizerMission() }
            .sortedByDescending { it.dateTime }
    }

    /**
     * Load details for a specific mission.
     */
    fun loadMissionDetail(missionId: String, onLoad: (OrganizerMissionDetail) -> Unit = {}) {
        val mission = MissionStorage.getById(missionId)
        if (mission != null) {
            selectedMission = mission.toOrganizerMission()
            onLoad(mission.toOrganizerDetail())
        } else {
            error = "Mission introuvable"
        }
    }

    /**
     * Create a new mission locally.
     */
    fun createMission(formData: MissionFormData, onSuccess: (String) -> Unit = {}) {
        isLoading = true
        error = null
        try {
            val theme = MissionTheme.valueOf(formData.theme)
            val mission = MissionStorage.create(
                title = formData.title,
                description = formData.description,
                theme = theme,
                dateTime = formData.dateTime,
                location = formData.location,
                maxParticipants = formData.maxParticipants,
                whatToBring = formData.whatToBring
            )
            loadMissions()
            onSuccess(mission.id)
        } catch (e: Exception) {
            error = "Erreur lors de la cr\u00e9ation: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    /**
     * Update an existing mission locally.
     */
    fun updateMission(missionId: String, formData: MissionFormData, onSuccess: () -> Unit = {}) {
        isLoading = true
        error = null
        try {
            val theme = MissionTheme.valueOf(formData.theme)
            MissionStorage.update(missionId) { existing ->
                existing.copy(
                    title = formData.title.takeIf { it.isNotBlank() } ?: existing.title,
                    description = formData.description.takeIf { it.isNotBlank() } ?: existing.description,
                    theme = theme,
                    dateTime = formData.dateTime.takeIf { it.isNotBlank() } ?: existing.dateTime,
                    location = formData.location.takeIf { it.isNotBlank() } ?: existing.location,
                    maxParticipants = if (formData.maxParticipants > 0) formData.maxParticipants else existing.maxParticipants,
                    whatToBring = formData.whatToBring
                )
            }
            loadMissions()
            onSuccess()
        } catch (e: Exception) {
            error = "Erreur lors de la mise \u00e0 jour: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    /**
     * Change mission status locally.
     */
    fun updateStatus(missionId: String, newStatus: String, onSuccess: () -> Unit = {}) {
        isLoading = true
        error = null
        try {
            val status = MissionStatus.valueOf(newStatus)
            MissionStorage.updateStatus(missionId, status)
            loadMissions()
            onSuccess()
        } catch (e: Exception) {
            error = "Erreur: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    /**
     * Publish missions to GitHub Pages.
     * Pushes the public missions JSON to the configured GitHub repository.
     */
    fun publishToGitHub() {
        scope.launch {
            isPublishing = true
            publishSuccess = null
            publishError = null
            try {
                val publicJson = MissionStorage.exportPublicJson()
                val result = GitHubPublisher.publish(publicJson)
                result.fold(
                    onSuccess = { message ->
                        publishSuccess = message
                    },
                    onFailure = { e ->
                        publishError = e.message
                    }
                )
            } catch (e: Exception) {
                publishError = "Erreur de publication: ${e.message}"
            } finally {
                isPublishing = false
            }
        }
    }

    fun clearError() {
        error = null
        pinError = null
    }

    fun clearPublishState() {
        publishSuccess = null
        publishError = null
    }

    /**
     * Convert shared Mission model to OrganizerMission view model.
     */
    private fun Mission.toOrganizerMission() = OrganizerMission(
        id = id,
        title = title,
        status = status.name,
        currentParticipants = currentParticipants,
        dateTime = dateTime
    )

    private fun Mission.toOrganizerDetail() = OrganizerMissionDetail(
        id = id,
        title = title,
        description = description,
        theme = theme.name,
        dateTime = dateTime,
        location = location,
        maxParticipants = maxParticipants,
        currentParticipants = currentParticipants,
        status = status.name,
        whatToBring = whatToBring
    )
}

// --- Data classes used by the organizer UI (kept for compatibility) ---

data class OrganizerMission(
    val id: String,
    val title: String,
    val status: String,
    val currentParticipants: Int,
    val dateTime: String
)

@kotlinx.serialization.Serializable
data class OrganizerMissionDetail(
    val id: String,
    val title: String,
    val description: String,
    val theme: String,
    val dateTime: String,
    val location: String,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val status: String,
    val whatToBring: String
)

data class MissionFormData(
    val title: String = "",
    val description: String = "",
    val theme: String = "RESOURCES",
    val dateTime: String = "",
    val location: String = "",
    val maxParticipants: Int = 10,
    val whatToBring: String = ""
)
