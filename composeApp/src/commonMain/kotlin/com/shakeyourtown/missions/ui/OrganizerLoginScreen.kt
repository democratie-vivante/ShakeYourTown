package com.shakeyourtown.missions.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * PIN-based authentication screen for organizer access.
 * First visit: setup PIN + organizer name.
 * Subsequent visits: enter PIN to unlock.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerLoginScreen(
    viewModel: OrganizerViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val isSetup = !viewModel.isPinSet()
    var pin by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.isAuthenticated) {
        if (viewModel.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSetup) "Configuration organisateur" else "Connexion organisateur") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Espace organisateur",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSetup)
                    "Configurez votre acc\u00e8s organisateur"
                else
                    "Saisissez votre code PIN",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isSetup) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Votre nom") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Organisation (optionnel)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
                label = { Text("Code PIN (4 chiffres)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                )
            )

            if (viewModel.pinError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.pinError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.clearError()
                    if (isSetup) {
                        viewModel.setupPin(pin, name.ifBlank { "Organisateur" }, organization)
                    } else {
                        viewModel.verifyPin(pin)
                    }
                },
                enabled = pin.length == 4 && (!isSetup || name.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSetup) "Configurer" else "Se connecter")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text("Retour aux missions")
            }
        }
    }
}
