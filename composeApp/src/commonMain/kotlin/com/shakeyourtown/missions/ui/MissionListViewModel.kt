package com.shakeyourtown.missions.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shakeyourtown.missions.models.Mission
import com.shakeyourtown.missions.models.MissionStatus
import com.shakeyourtown.missions.models.MissionTheme
import com.shakeyourtown.missions.storage.SettingsStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * ViewModel for the citizen-facing side.
 * Fetches missions from a static JSON URL (GitHub Pages).
 * Submits signups to a Google Forms endpoint.
 */
class MissionsViewModel {
    // --- List state ---
    var missions by mutableStateOf<List<PublicMission>>(emptyList())
    var selectedTheme by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // --- Detail state ---
    var selectedMission by mutableStateOf<PublicMission?>(null)
    var isDetailLoading by mutableStateOf(false)
    var detailError by mutableStateOf<String?>(null)

    // --- Signup state ---
    var isSignupLoading by mutableStateOf(false)
    var signupError by mutableStateOf<String?>(null)
    var signupSuccess by mutableStateOf(false)

    private val scope = CoroutineScope(Dispatchers.Main)
    private val jsonParser = Json { ignoreUnknownKeys = true }

    companion object {
        private val httpClient by lazy {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
    }

    /**
     * Fetch missions from the static JSON file hosted on GitHub Pages.
     */
    fun loadMissions() {
        scope.launch {
            isLoading = true
            error = null
            try {
                val settings = SettingsStorage.load()
                val url = settings.missionsJsonUrl
                if (url.isBlank()) {
                    error = "URL des missions non configurée. Contactez l'organisateur."
                    missions = emptyList()
                    return@launch
                }

                val responseText: String = httpClient.get(url).bodyAsText()
                val allMissions: List<PublicMission> = jsonParser.decodeFromString(responseText)

                missions = allMissions
                    .filter { it.status == "PUBLISHED" || it.status == "FULL" }
                    .let { list ->
                        if (selectedTheme != null) list.filter { it.theme == selectedTheme } else list
                    }
                    .sortedBy { it.dateTime }
            } catch (e: Exception) {
                error = "Impossible de charger les missions: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun setThemeFilter(theme: String?) {
        selectedTheme = theme
        loadMissions()
    }

    fun loadMissionDetail(missionId: String) {
        scope.launch {
            isDetailLoading = true
            detailError = null
            selectedMission = null
            try {
                // Mission data is already loaded; find it in the local list
                selectedMission = missions.find { it.id == missionId }
                if (selectedMission == null) {
                    // Try re-fetching from remote
                    val settings = SettingsStorage.load()
                    val url = settings.missionsJsonUrl
                    if (url.isNotBlank()) {
                        val responseText: String = httpClient.get(url).bodyAsText()
                        val allMissions: List<PublicMission> = jsonParser.decodeFromString(responseText)
                        selectedMission = allMissions.find { it.id == missionId }
                    }
                    if (selectedMission == null) {
                        detailError = "Mission introuvable"
                    }
                }
            } catch (e: Exception) {
                detailError = e.message
            } finally {
                isDetailLoading = false
            }
        }
    }

    /**
     * Submit signup to Google Forms.
     * Google Forms accepts POST to the formResponse URL with entry.XXXXX fields.
     */
    fun submitSignup(missionId: String, name: String, email: String, phone: String) {
        scope.launch {
            isSignupLoading = true
            signupError = null
            signupSuccess = false
            try {
                val settings = SettingsStorage.load()
                if (settings.googleFormUrl.isBlank()) {
                    signupError = "Le formulaire d'inscription n'est pas configuré."
                    return@launch
                }

                val missionTitle = selectedMission?.title ?: missionId

                // Build the Google Forms submission URL
                val formUrl = settings.googleFormUrl
                val params = buildString {
                    if (settings.googleFormEntryName.isNotBlank()) {
                        append("${settings.googleFormEntryName}=${encodeParam(name)}")
                    }
                    if (settings.googleFormEntryEmail.isNotBlank() && email.isNotBlank()) {
                        append("&${settings.googleFormEntryEmail}=${encodeParam(email)}")
                    }
                    if (settings.googleFormEntryPhone.isNotBlank() && phone.isNotBlank()) {
                        append("&${settings.googleFormEntryPhone}=${encodeParam(phone)}")
                    }
                    if (settings.googleFormEntryMissionId.isNotBlank()) {
                        append("&${settings.googleFormEntryMissionId}=${encodeParam(missionId)}")
                    }
                    if (settings.googleFormEntryMissionTitle.isNotBlank()) {
                        append("&${settings.googleFormEntryMissionTitle}=${encodeParam(missionTitle)}")
                    }
                }

                val response = httpClient.post(formUrl) {
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody(params)
                }

                // Google Forms always returns 200 even on success
                signupSuccess = true
            } catch (e: Exception) {
                signupError = "Erreur lors de l'inscription: ${e.message}"
            } finally {
                isSignupLoading = false
            }
        }
    }

    fun resetSignupState() {
        signupError = null
        signupSuccess = false
        isSignupLoading = false
    }

    fun resetDetailState() {
        selectedMission = null
        detailError = null
        isDetailLoading = false
    }

    private fun encodeParam(value: String): String {
        return value
            .replace("%", "%25")
            .replace(" ", "+")
            .replace("&", "%26")
            .replace("=", "%3D")
            .replace("+", "%2B")
            .replace("#", "%23")
            .replace("@", "%40")
    }
}

/**
 * Mission as fetched from the static JSON file (public-facing).
 * Uses String types for theme/status to match the JSON output from shared models.
 */
@Serializable
data class PublicMission(
    val id: String,
    val title: String,
    val description: String,
    val theme: String,
    val dateTime: String,
    val location: String,
    val maxParticipants: Int,
    val currentParticipants: Int = 0,
    val status: String,
    val whatToBring: String = "",
    val organizerId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val completionSummary: String = "",
    val actualParticipants: Int = 0
)
