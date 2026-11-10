package com.ptilou.app.feature.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouOrange
import com.ptilou.app.core.ui.theme.PtilouSurface

@Composable
fun MemoryScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: MemoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pieces by viewModel.pieces.collectAsState()
    val creditsJeu by viewModel.creditsJeu.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = pieces)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Jeu de Ptilou : Mémory", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "${uiState.pairesTrouvees} / ${uiState.cartes.size / 2} paires trouvées",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                uiState.cartes.chunked(4).forEach { ligne ->
                    Row {
                        ligne.forEach { carte ->
                            CarteMemoryVue(
                                carte = carte,
                                onClick = { viewModel.retournerCarte(carte.id) }
                            )
                        }
                    }
                }

                if (uiState.jeuTermine) {
                    Text(
                        text = "Bravo, tu as trouvé toutes les paires ! 🎉",
                        color = PtilouOrange,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    if (creditsJeu > 0) {
                        Button(onClick = { viewModel.rejouer() }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Rejouer ($creditsJeu partie${if (creditsJeu > 1) "s" else ""} restante${if (creditsJeu > 1) "s" else ""})")
                        }
                    } else {
                        Text(
                            text = "Tu as utilisé tes parties ! Continue à faire des exercices pour en débloquer d'autres.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CarteMemoryVue(carte: CarteMemory, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(64.dp)
            .clickable(enabled = !carte.retournee && !carte.trouvee, onClick = onClick)
            .background(
                color = if (carte.trouvee) PtilouGreen.copy(alpha = 0.35f) else PtilouSurface,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (carte.retournee || carte.trouvee) carte.symbole else "❓",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
