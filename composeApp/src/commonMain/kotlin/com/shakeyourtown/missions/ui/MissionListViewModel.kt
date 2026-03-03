package com.shakeyourtown.missions.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

class MissionsViewModel(
    private val apiBaseUrl: String = "http://10.0.2.2:8080"
) {
    // --- List state ---
    var missions by mutableStateOf<List<Mission>>(emptyList())
    var selectedTheme by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // --- Detail state ---
    var selectedMission by mutableStateOf<Mission?>(null)
    var isDetailLoading by mutableStateOf(false)
    var detailError by mutableStateOf<String?>(null)

    // --- Signup state ---
    var isSignupLoading by mutableStateOf(false)
    var signupError by mutableStateOf<String?>(null)
    var signupSuccess by mutableStateOf(false)

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private val httpClient by lazy {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
    }

    fun loadMissions() {
        scope.launch {
            isLoading = true
            error = null
            try {
                val themeParam = if (selectedTheme != null) "&theme=$selectedTheme" else ""
                val url = "$apiBaseUrl/api/v1/missions?upcoming=true$themeParam"
                val response: MissionListResponse = httpClient.get(url).body()
                missions = response.missions
            } catch (e: Exception) {
                error = e.message
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
                val url = "$apiBaseUrl/api/v1/missions/$missionId"
                selectedMission = httpClient.get(url).body()
            } catch (e: Exception) {
                detailError = e.message
            } finally {
                isDetailLoading = false
            }
        }
    }

    fun submitSignup(missionId: String, name: String, email: String, phone: String) {
        scope.launch {
            isSignupLoading = true
            signupError = null
            signupSuccess = false
            try {
                val url = "$apiBaseUrl/api/v1/missions/$missionId/signup"
                val request = SignupRequest(
                    participantName = name,
                    contactEmail = email.ifBlank { null },
                    contactPhone = phone.ifBlank { null }
                )
                val response = httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                if (response.status.value in 200..299) {
                    signupSuccess = true
                } else {
                    val errorBody = response.bodyAsText()
                    signupError = errorBody
                }
            } catch (e: Exception) {
                signupError = e.message
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
}

@Serializable
data class MissionListResponse(
    val missions: List<Mission>
)

@Serializable
data class SignupRequest(
    val participantName: String,
    val contactEmail: String?,
    val contactPhone: String?
)
