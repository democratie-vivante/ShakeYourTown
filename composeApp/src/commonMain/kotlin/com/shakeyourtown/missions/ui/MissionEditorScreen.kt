package com.shakeyourtown.missions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionEditorScreen(
    viewModel: OrganizerViewModel,
    missionId: String?,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf("RESOURCES") }
    var dateTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("10") }
    var whatToBring by remember { mutableStateOf("") }
    
    val isEditMode = missionId != null

    LaunchedEffect(missionId) {
        if (missionId != null) {
            viewModel.loadMissionDetail(missionId) { mission ->
                title = mission.title
                description = mission.description
                selectedTheme = mission.theme
                dateTime = mission.dateTime
                location = mission.location
                maxParticipants = mission.maxParticipants.toString()
                whatToBring = mission.whatToBring
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Modifier la mission" else "Nouvelle mission") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank() && title.isNotEmpty()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Th\u00e8me *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTheme == "MOBILITY",
                    onClick = { selectedTheme = "MOBILITY" },
                    label = { Text("Mobilit\u00e9") }
                )
                FilterChip(
                    selected = selectedTheme == "RESOURCES",
                    onClick = { selectedTheme = "RESOURCES" },
                    label = { Text("Ressources") }
                )
                FilterChip(
                    selected = selectedTheme == "FOOD",
                    onClick = { selectedTheme = "FOOD" },
                    label = { Text("Alimentation") }
                )
            }

            OutlinedTextField(
                value = dateTime,
                onValueChange = { dateTime = it },
                label = { Text("Date et heure *") },
                placeholder = { Text("2026-04-15T10:00:00") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxParticipants,
                onValueChange = { maxParticipants = it.filter { c -> c.isDigit() } },
                label = { Text("Nombre maximum de participants *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = whatToBring,
                onValueChange = { whatToBring = it },
                label = { Text("\u00c0 apporter (optionnel)") },
                minLines = 2,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (viewModel.error != null) {
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val formData = MissionFormData(
                        title = title,
                        description = description,
                        theme = selectedTheme,
                        dateTime = dateTime,
                        location = location,
                        maxParticipants = maxParticipants.toIntOrNull() ?: 10,
                        whatToBring = whatToBring
                    )
                    if (isEditMode) {
                        viewModel.updateMission(missionId, formData, onSaveSuccess)
                    } else {
                        viewModel.createMission(formData) { onSaveSuccess() }
                    }
                },
                enabled = title.isNotBlank() && 
                         description.isNotBlank() && 
                         dateTime.isNotBlank() && 
                         location.isNotBlank() &&
                         !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (isEditMode) "Enregistrer" else "Cr\u00e9er")
                }
            }

            Text(
                text = "* champs obligatoires",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
