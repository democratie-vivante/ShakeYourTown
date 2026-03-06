package io.mbras.syt

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.shakeyourtown.missions.ui.*
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class Screen {
    MissionList,
    MissionDetail,
    Signup,
    OrganizerLogin,
    OrganizerDashboard,
    OrganizerMissionDetail,
    MissionEditor,
    Settings,
    Archive
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = remember { MissionsViewModel() }
        val organizerViewModel = remember { OrganizerViewModel() }
        var currentScreen by remember { mutableStateOf(Screen.MissionList) }
        var selectedMissionId by remember { mutableStateOf<String?>(null) }
        var editingMissionId by remember { mutableStateOf<String?>(null) }

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
                    onNavigateToArchive = { 
                        currentScreen = Screen.Archive 
                    },
                    isLoading = viewModel.isLoading,
                    onNavigateToOrganizer = {
                        if (organizerViewModel.isAuthenticated) {
                            currentScreen = Screen.OrganizerDashboard
                        } else {
                            currentScreen = Screen.OrganizerLogin
                        }
                    }
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

            Screen.OrganizerLogin -> {
                OrganizerLoginScreen(
                    viewModel = organizerViewModel,
                    onLoginSuccess = {
                        currentScreen = Screen.OrganizerDashboard
                    },
                    onNavigateBack = {
                        currentScreen = Screen.MissionList
                    }
                )
            }

            Screen.OrganizerDashboard -> {
                OrganizerDashboardScreen(
                    viewModel = organizerViewModel,
                    onNavigateToEditor = { missionId ->
                        editingMissionId = missionId
                        currentScreen = Screen.MissionEditor
                    },
                    onNavigateToMissionDetail = { missionId ->
                        selectedMissionId = missionId
                        currentScreen = Screen.OrganizerMissionDetail
                    },
                    onNavigateToSettings = {
                        currentScreen = Screen.Settings
                    },
                    onLogout = {
                        organizerViewModel.logout {
                            currentScreen = Screen.MissionList
                        }
                    }
                )
            }

            Screen.OrganizerMissionDetail -> {
                selectedMissionId?.let { missionId ->
                    OrganizerMissionDetailScreen(
                        viewModel = organizerViewModel,
                        missionId = missionId,
                        onNavigateToEditor = { id ->
                            editingMissionId = id
                            currentScreen = Screen.MissionEditor
                        },
                        onNavigateBack = {
                            currentScreen = Screen.OrganizerDashboard
                        }
                    )
                }
            }

            Screen.MissionEditor -> {
                MissionEditorScreen(
                    viewModel = organizerViewModel,
                    missionId = editingMissionId,
                    onSaveSuccess = {
                        organizerViewModel.loadMissions()
                        currentScreen = Screen.OrganizerDashboard
                    },
                    onNavigateBack = {
                        currentScreen = if (editingMissionId != null) {
                            Screen.OrganizerMissionDetail
                        } else {
                            Screen.OrganizerDashboard
                        }
                    }
                )
            }

            Screen.Settings -> {
                SettingsScreen(
                    onNavigateBack = {
                        currentScreen = Screen.OrganizerDashboard
                    }
                )
            }

            Screen.Archive -> {
                ArchiveScreen(
                    onNavigateBack = {
                        currentScreen = Screen.MissionList
                    }
                )
            }
        }
    }
}
