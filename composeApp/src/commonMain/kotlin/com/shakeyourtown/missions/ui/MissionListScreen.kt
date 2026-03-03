package com.shakeyourtown.missions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
data class Mission(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreen(
    missions: List<Mission>,
    selectedTheme: String?,
    onThemeSelected: (String?) -> Unit,
    onMissionClick: (String) -> Unit,
    onNavigateToArchive: () -> Unit,
    isLoading: Boolean = false
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Missions citoyennes") },
                actions = {
                    TextButton(onClick = onNavigateToArchive) {
                        Text("Archives")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Theme filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTheme == null,
                    onClick = { onThemeSelected(null) },
                    label = { Text("Toutes") }
                )
                FilterChip(
                    selected = selectedTheme == "MOBILITY",
                    onClick = { onThemeSelected("MOBILITY") },
                    label = { Text("Mobilit\u00e9") }
                )
                FilterChip(
                    selected = selectedTheme == "RESOURCES",
                    onClick = { onThemeSelected("RESOURCES") },
                    label = { Text("Ressources") }
                )
                FilterChip(
                    selected = selectedTheme == "FOOD",
                    onClick = { onThemeSelected("FOOD") },
                    label = { Text("Alimentation") }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (missions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Aucune mission disponible",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(missions) { mission ->
                        MissionCard(
                            mission = mission,
                            onClick = { onMissionClick(mission.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MissionCard(
    mission: Mission,
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
                    style = MaterialTheme.typography.titleMedium
                )
                MissionStatusChip(mission.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = themeLabel(mission.theme),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mission.description.take(100) + if (mission.description.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mission.dateTime.take(16).replace("T", " "),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${mission.currentParticipants}/${mission.maxParticipants} participants",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MissionStatusChip(status: String) {
    val label = statusLabel(status)
    val (containerColor, textColor) = when (status) {
        "PUBLISHED" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "FULL" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "CANCELLED" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "DONE" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
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

internal fun themeLabel(theme: String): String = when (theme) {
    "MOBILITY" -> "Mobilit\u00e9"
    "RESOURCES" -> "Ressources"
    "FOOD" -> "Alimentation"
    else -> theme
}

internal fun statusLabel(status: String): String = when (status) {
    "PUBLISHED" -> "Publi\u00e9e"
    "FULL" -> "Compl\u00e8te"
    "CANCELLED" -> "Annul\u00e9e"
    "DONE" -> "Termin\u00e9e"
    "DRAFT" -> "Brouillon"
    else -> status
}
