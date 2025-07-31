package io.mbras.syt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.mbras.syt.ui.ProposalListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val repository = remember { ProposalRepository() }
        
        ProposalListScreen(
            repository = repository,
            modifier = Modifier.fillMaxSize()
        )
    }
}
