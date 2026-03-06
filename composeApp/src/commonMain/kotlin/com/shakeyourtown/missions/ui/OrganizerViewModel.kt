package com.shakeyourtown.missions.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.patch
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

class OrganizerViewModel(
    private val apiBaseUrl: String = "http://10.0.2.2:8080"
) {
    var isLoggedIn by mutableStateOf(false)
        private set
    var organizerName by mutableStateOf("")
        private set
    var organizerOrganization by mutableStateOf("")
        private set
    
    var missions by mutableStateOf<List<OrganizerMission>>(emptyList())
        private set
    var selectedMission by mutableStateOf<OrganizerMission?>(null)
        private set
    var signups by mutableStateOf<List<OrganizerSignup>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var loginError by mutableStateOf<String?>(null)
        private set

    private var sessionToken: String? = null
    
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

    fun login(username: String, password: String, onSuccess: () -> Unit = {}) {
        scope.launch {
            isLoading = true
            loginError = null
            try {
                val response = httpClient.post("$apiBaseUrl/api/v1/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(username, password))
                }
                if (response.status.value in 200..299) {
                    val loginResponse: LoginSuccessResponse = response.body()
                    isLoggedIn = true
                    organizerName = loginResponse.organizer?.name ?: ""
                    organizerOrganization = loginResponse.organizer?.organization ?: ""
                    sessionToken = response.headers["Set-Cookie"]?.let { 
                        """session=([a-f0-9-]+)""".toRegex().find(it)?.groupValues?.get(1) 
                    }
                    onSuccess()
                } else {
                    val errorBody = response.bodyAsText()
                    loginError = extractErrorMessage(errorBody)
                }
            } catch (e: Exception) {
                loginError = e.message ?: "Erreur de connexion"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        scope.launch {
            try {
                httpClient.post("$apiBaseUrl/api/v1/auth/logout")
            } catch (e: Exception) {
            }
            isLoggedIn = false
            organizerName = ""
            organizerOrganization = ""
            sessionToken = null
            missions = emptyList()
            selectedMission = null
            signups = emptyList()
            onComplete()
        }
    }

    fun loadMissions() {
        scope.launch {
            isLoading = true
            error = null
            try {
                val response: OrganizerMissionListResponse = httpClient.get("$apiBaseUrl/api/v1/organizer/missions").body()
                missions = response.missions.map { 
                    OrganizerMission(
                        id = it["id"] ?: "",
                        title = it["title"] ?: "",
                        status = it["status"] ?: "",
                        currentParticipants = it["currentParticipants"]?.toIntOrNull() ?: 0,
                        dateTime = it["dateTime"] ?: ""
                    )
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMissionDetail(missionId: String, onLoad: (OrganizerMissionDetail) -> Unit = {}) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val response: OrganizerMissionDetail = httpClient.get("$apiBaseUrl/api/v1/organizer/missions/$missionId").body()
                selectedMission = OrganizerMission(
                    id = response.id,
                    title = response.title,
                    status = response.status,
                    currentParticipants = response.currentParticipants,
                    dateTime = response.dateTime
                )
                onLoad(response)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun createMission(mission: MissionFormData, onSuccess: (String) -> Unit = {}) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val response = httpClient.post("$apiBaseUrl/api/v1/organizer/missions") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateMissionRequest(
                        title = mission.title,
                        description = mission.description,
                        theme = mission.theme,
                        dateTime = mission.dateTime,
                        location = mission.location,
                        maxParticipants = mission.maxParticipants,
                        whatToBring = mission.whatToBring
                    ))
                }
                if (response.status.value in 200..299) {
                    val result: MissionCreateResponse = response.body()
                    onSuccess(result.id)
                } else {
                    error = response.bodyAsText()
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun updateMission(missionId: String, mission: MissionFormData, onSuccess: () -> Unit = {}) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val response = httpClient.put("$apiBaseUrl/api/v1/organizer/missions/$missionId") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdateMissionRequest(
                        title = mission.title.takeIf { it.isNotBlank() },
                        description = mission.description.takeIf { it.isNotBlank() },
                        theme = mission.theme.takeIf { it.isNotBlank() },
                        dateTime = mission.dateTime.takeIf { it.isNotBlank() },
                        location = mission.location.takeIf { it.isNotBlank() },
                        maxParticipants = mission.maxParticipants.takeIf { it > 0 },
                        whatToBring = mission.whatToBring.takeIf { it.isNotBlank() }
                    ))
                }
                if (response.status.value in 200..299) {
                    onSuccess()
                } else {
                    error = response.bodyAsText()
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun updateStatus(missionId: String, newStatus: String, onSuccess: () -> Unit = {}) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val response = httpClient.patch("$apiBaseUrl/api/v1/organizer/missions/$missionId/status") {
                    contentType(ContentType.Application.Json)
                    setBody(StatusUpdateRequest(newStatus))
                }
                if (response.status.value in 200..299) {
                    loadMissions()
                    onSuccess()
                } else {
                    error = response.bodyAsText()
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun loadSignups(missionId: String) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val response: SignupListResponse = httpClient.get("$apiBaseUrl/api/v1/organizer/missions/$missionId/signups").body()
                signups = response.signups.map {
                    OrganizerSignup(
                        id = it["id"] ?: "",
                        participantName = it["participantName"] ?: "",
                        contactEmail = it["contactEmail"] ?: "",
                        contactPhone = it["contactPhone"] ?: "",
                        signedUpAt = it["signedUpAt"] ?: "",
                        status = it["status"] ?: ""
                    )
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        error = null
        loginError = null
    }

    private fun extractErrorMessage(json: String): String {
        return try {
            val obj = Json.decodeFromString<Map<String, String>>(json)
            obj["message"] ?: json
        } catch (e: Exception) {
            json
        }
    }
}

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginSuccessResponse(val success: Boolean, val organizer: OrganizerInfo?)

@Serializable
data class OrganizerInfo(val id: String, val name: String, val organization: String)

@Serializable
data class OrganizerMissionListResponse(val missions: List<Map<String, String>>)

@Serializable
data class SignupListResponse(val signups: List<Map<String, String>>)

@Serializable
data class MissionCreateResponse(val id: String, val status: String)

data class OrganizerMission(
    val id: String,
    val title: String,
    val status: String,
    val currentParticipants: Int,
    val dateTime: String
)

@Serializable
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

data class OrganizerSignup(
    val id: String,
    val participantName: String,
    val contactEmail: String,
    val contactPhone: String,
    val signedUpAt: String,
    val status: String
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

@Serializable
data class CreateMissionRequest(
    val title: String,
    val description: String,
    val theme: String,
    val dateTime: String,
    val location: String,
    val maxParticipants: Int,
    val whatToBring: String? = null
)

@Serializable
data class UpdateMissionRequest(
    val title: String? = null,
    val description: String? = null,
    val theme: String? = null,
    val dateTime: String? = null,
    val location: String? = null,
    val maxParticipants: Int? = null,
    val whatToBring: String? = null
)

@Serializable
data class StatusUpdateRequest(val status: String)
