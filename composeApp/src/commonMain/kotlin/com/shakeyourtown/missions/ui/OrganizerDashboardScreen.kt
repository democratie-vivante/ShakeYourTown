package com.shakeyourtown.missions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerDashboardScreen(
    viewModel: OrganizerViewModel,
    onNavigateToEditor: (String?) -> Unit,
    onNavigateToMissionDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadMissions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tableau de bord") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Param\u00e8tres")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "D\u00e9connexion")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEditor(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle mission")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Welcome card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Bienvenue, ${viewModel.organizerName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (viewModel.organizerOrganization.isNotBlank()) {
                        Text(
                            text = viewModel.organizerOrganization,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Publish banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Publication",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Publiez vos missions pour les rendre visibles aux citoyens",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.publishSuccess != null) {
                        Text(
                            text = viewModel.publishSuccess!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (viewModel.publishError != null) {
                        Text(
                            text = viewModel.publishError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.clearPublishState()
                            viewModel.publishToGitHub()
                        },
                        enabled = !viewModel.isPublishing
                    ) {
                        if (viewModel.isPublishing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Publier sur GitHub Pages")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mission list
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.missions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aucune mission",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Cr\u00e9ez votre premi\u00e8re mission",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.missions) { mission ->
                        OrganizerMissionCard(
                            mission = mission,
                            onClick = { onNavigateToMissionDetail(mission.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizerMissionCard(
    mission: OrganizerMission,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                OrganizerStatusChip(mission.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mission.dateTime.take(16).replace("T", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${mission.currentParticipants} participants",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OrganizerStatusChip(status: String) {
    val label = statusLabel(status)
    val containerColor = when (status) {
        "DRAFT" -> MaterialTheme.colorScheme.surfaceVariant
        "PUBLISHED" -> MaterialTheme.colorScheme.primaryContainer
        "FULL" -> MaterialTheme.colorScheme.secondaryContainer
        "CANCELLED" -> MaterialTheme.colorScheme.errorContainer
        "DONE" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (status) {
        "DRAFT" -> MaterialTheme.colorScheme.onSurfaceVariant
        "PUBLISHED" -> MaterialTheme.colorScheme.onPrimaryContainer
        "FULL" -> MaterialTheme.colorScheme.onSecondaryContainer
        "CANCELLED" -> MaterialTheme.colorScheme.onErrorContainer
        "DONE" -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerMissionDetailScreen(
    viewModel: OrganizerViewModel,
    missionId: String,
    onNavigateToEditor: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(missionId) {
        viewModel.loadMissionDetail(missionId)
    }

    val mission = viewModel.selectedMission

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mission?.title ?: "D\u00e9tail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (mission == null && viewModel.isLoading) {
                CircularProgressIndicator()
            } else if (mission != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OrganizerStatusChip(mission.status)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Participants: ${mission.currentParticipants}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Les inscriptions arrivent dans votre Google Sheets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigateToEditor(missionId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Modifier")
                    }

                    if (mission.status == "DRAFT") {
                        Button(
                            onClick = { 
                                viewModel.updateStatus(missionId, "PUBLISHED", onNavigateBack)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Publier")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (mission.status in listOf("PUBLISHED", "FULL")) {
                        OutlinedButton(
                            onClick = { 
                                viewModel.updateStatus(missionId, "CANCELLED", onNavigateBack)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Annuler")
                        }

                        Button(
                            onClick = { 
                                viewModel.updateStatus(missionId, "DONE", onNavigateBack)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Termin\u00e9e")
                        }
                    }
                }
            }

            if (viewModel.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
