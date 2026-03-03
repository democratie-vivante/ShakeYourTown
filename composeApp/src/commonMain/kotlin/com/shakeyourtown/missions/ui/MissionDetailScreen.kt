package com.shakeyourtown.missions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    mission: Mission?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSignupClick: () -> Unit,
    error: String? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mission?.title ?: "D\u00e9tail de la mission") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                mission == null -> {
                    Text(
                        text = "Mission introuvable",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                mission.status == "CANCELLED" -> {
                    Text(
                        text = "Cette mission a \u00e9t\u00e9 annul\u00e9e",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        MissionStatusChip(mission.status)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = themeLabel(mission.theme),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = mission.description,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DetailRow(label = "Date et heure", value = mission.dateTime.replace("T", " "))
                        DetailRow(label = "Lieu", value = mission.location)
                        DetailRow(
                            label = "Participants",
                            value = "${mission.currentParticipants}/${mission.maxParticipants}"
                        )

                        if (mission.whatToBring.isNotBlank()) {
                            DetailRow(label = "\u00c0 apporter", value = mission.whatToBring)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (mission.status == "FULL") {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Mission compl\u00e8te")
                            }
                        } else {
                            Button(
                                onClick = onSignupClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("S'inscrire")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
