package io.mbras.syt

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.shakeyourtown.missions.ui.MissionDetailScreen
import com.shakeyourtown.missions.ui.MissionListScreen
import com.shakeyourtown.missions.ui.MissionsViewModel
import com.shakeyourtown.missions.ui.SignupFormScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class Screen {
    MissionList,
    MissionDetail,
    Signup
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = remember { MissionsViewModel() }
        var currentScreen by remember { mutableStateOf(Screen.MissionList) }
        var selectedMissionId by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            viewModel.loadMissions()
        }

        when (currentScreen) {
            Screen.MissionList -> {
                MissionListScreen(
                    missions = viewModel.missions,
                    selectedTheme = viewModel.selectedTheme,
                    onThemeSelected = { viewModel.setThemeFilter(it) },
                    onMissionClick = { missionId ->
                        selectedMissionId = missionId
                        viewModel.loadMissionDetail(missionId)
                        currentScreen = Screen.MissionDetail
                    },
                    onNavigateToArchive = { /* Phase 5 - pas encore implemente */ },
                    isLoading = viewModel.isLoading
                )
            }

            Screen.MissionDetail -> {
                MissionDetailScreen(
                    mission = viewModel.selectedMission,
                    isLoading = viewModel.isDetailLoading,
                    error = viewModel.detailError,
                    onBackClick = {
                        viewModel.resetDetailState()
                        currentScreen = Screen.MissionList
                    },
                    onSignupClick = {
                        viewModel.resetSignupState()
                        currentScreen = Screen.Signup
                    }
                )
            }

            Screen.Signup -> {
                SignupFormScreen(
                    missionTitle = viewModel.selectedMission?.title ?: "",
                    onSubmit = { name, email, phone ->
                        selectedMissionId?.let { id ->
                            viewModel.submitSignup(id, name, email, phone)
                        }
                    },
                    onCancel = {
                        viewModel.resetSignupState()
                        currentScreen = Screen.MissionDetail
                    },
                    isLoading = viewModel.isSignupLoading,
                    error = viewModel.signupError,
                    success = viewModel.signupSuccess
                )
            }
        }
    }
}
