package com.shakeyourtown.missions.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MissionListViewModel(
    private val apiBaseUrl: String = "http://localhost:8080"
) {
    var missions by mutableStateOf<List<Mission>>(emptyList())
    var selectedTheme by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private val httpClient by lazy { ktor.client.HttpClient() }
    }

    fun loadMissions() {
        scope.launch {
            isLoading = true
            error = null
            try {
                val themeParam = if (selectedTheme != null) "&theme=$selectedTheme" else ""
                val url = "$apiBaseUrl/api/v1/missions?upcoming=true$themeParam"
                val response = httpClient.get(url)
                missions = response.body<MissionListResponse>().missions
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
}

@kotlinx.serialization.Serializable
data class MissionListResponse(
    val missions: List<Mission>
)
