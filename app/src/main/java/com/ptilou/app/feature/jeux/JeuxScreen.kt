package com.ptilou.app.feature.jeux

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.BarreSuperieure

@Composable
fun JeuxScreen(
    onOuvrirMemory: () -> Unit,
    onOuvrirBulles: () -> Unit,
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: JeuxViewModel = hiltViewModel()
) {
    val pieces by viewModel.pieces.collectAsState()
    val credits by viewModel.credits.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = pieces)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Choisis ton jeu", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "$credits partie${if (credits > 1) "s" else ""} disponible${if (credits > 1) "s" else ""}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Button(
                        onClick = { viewModel.consommerPartie(); onOuvrirMemory() },
                        enabled = credits > 0
                    ) {
                        Text("🃏 Mémory", style = MaterialTheme.typography.titleLarge)
                    }
                    Button(
                        onClick = { viewModel.consommerPartie(); onOuvrirBulles() },
                        enabled = credits > 0
                    ) {
                        Text("🫧 Bulles", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
