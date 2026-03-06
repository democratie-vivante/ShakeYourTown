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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.shakeyourtown.missions.storage.AppSettings
import com.shakeyourtown.missions.storage.SettingsStorage

/**
 * Settings screen for the organizer to configure:
 * - GitHub repository (owner, repo name, PAT) for publishing missions
 * - Google Forms URL + entry field IDs for citizen signups
 * - Static missions JSON URL (what citizens fetch)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var settings by remember { mutableStateOf(SettingsStorage.load()) }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Param\u00e8tres") },
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
            // --- GitHub Pages section ---
            Text(
                text = "Publication GitHub Pages",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Les missions sont publi\u00e9es sous forme de fichier JSON sur un d\u00e9p\u00f4t GitHub. Les citoyens acc\u00e8dent aux missions via l'URL GitHub Pages.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            OutlinedTextField(
                value = settings.githubOwner,
                onValueChange = { settings = settings.copy(githubOwner = it) },
                label = { Text("Propri\u00e9taire GitHub") },
                placeholder = { Text("mon-organisation") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.githubRepo,
                onValueChange = { settings = settings.copy(githubRepo = it) },
                label = { Text("Nom du d\u00e9p\u00f4t") },
                placeholder = { Text("shakeyourtown") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.githubFilePath,
                onValueChange = { settings = settings.copy(githubFilePath = it) },
                label = { Text("Chemin du fichier") },
                placeholder = { Text("data/missions.json") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.githubToken,
                onValueChange = { settings = settings.copy(githubToken = it) },
                label = { Text("Token GitHub (PAT)") },
                placeholder = { Text("ghp_xxxx...") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            HorizontalDivider()

            // --- Missions URL section ---
            Text(
                text = "URL des missions (pour les citoyens)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = settings.missionsJsonUrl,
                onValueChange = { settings = settings.copy(missionsJsonUrl = it) },
                label = { Text("URL du fichier missions.json") },
                placeholder = { Text("https://mon-org.github.io/shakeyourtown/data/missions.json") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // --- Google Forms section ---
            Text(
                text = "Inscriptions (Google Forms)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Les inscriptions des citoyens sont envoy\u00e9es \u00e0 un Google Form. Les r\u00e9ponses apparaissent dans le Google Sheets li\u00e9.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pour trouver les entry IDs, ouvrez votre formulaire, faites \"Obtenir le lien pr\u00e9rempli\" et copiez les noms de champs (entry.XXXXX).",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            OutlinedTextField(
                value = settings.googleFormUrl,
                onValueChange = { settings = settings.copy(googleFormUrl = it) },
                label = { Text("URL du formulaire (formResponse)") },
                placeholder = { Text("https://docs.google.com/forms/d/e/xxx/formResponse") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.googleFormEntryName,
                onValueChange = { settings = settings.copy(googleFormEntryName = it) },
                label = { Text("Entry ID - Nom") },
                placeholder = { Text("entry.123456789") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.googleFormEntryEmail,
                onValueChange = { settings = settings.copy(googleFormEntryEmail = it) },
                label = { Text("Entry ID - Email") },
                placeholder = { Text("entry.123456790") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.googleFormEntryPhone,
                onValueChange = { settings = settings.copy(googleFormEntryPhone = it) },
                label = { Text("Entry ID - T\u00e9l\u00e9phone") },
                placeholder = { Text("entry.123456791") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.googleFormEntryMissionId,
                onValueChange = { settings = settings.copy(googleFormEntryMissionId = it) },
                label = { Text("Entry ID - ID Mission") },
                placeholder = { Text("entry.123456792") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.googleFormEntryMissionTitle,
                onValueChange = { settings = settings.copy(googleFormEntryMissionTitle = it) },
                label = { Text("Entry ID - Titre Mission") },
                placeholder = { Text("entry.123456793") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // --- Organizer identity ---
            Text(
                text = "Identit\u00e9 organisateur",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = settings.organizerName,
                onValueChange = { settings = settings.copy(organizerName = it) },
                label = { Text("Votre nom") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.organizerOrganization,
                onValueChange = { settings = settings.copy(organizerOrganization = it) },
                label = { Text("Organisation") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = {
                    SettingsStorage.save(settings)
                    saved = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer")
            }

            if (saved) {
                Text(
                    text = "Param\u00e8tres enregistr\u00e9s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
