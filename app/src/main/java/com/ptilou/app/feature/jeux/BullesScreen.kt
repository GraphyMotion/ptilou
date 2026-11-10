package com.ptilou.app.feature.jeux

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.BarreSuperieure
import com.ptilou.app.core.ui.theme.PtilouBlue
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouOrange
import com.ptilou.app.core.ui.theme.PtilouRed
import com.ptilou.app.core.ui.theme.PtilouYellow

private val couleursBulles = listOf(PtilouOrange, PtilouBlue, PtilouGreen, PtilouYellow, PtilouRed)
private val TAILLE_BULLE = 76.dp

@Composable
fun BullesScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: BullesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pieces by viewModel.pieces.collectAsState()
    val credits by viewModel.credits.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = pieces)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🫧 ${uiState.score} bulles", style = MaterialTheme.typography.titleLarge)
                Text("⏱️ ${uiState.secondesRestantes} s", style = MaterialTheme.typography.titleLarge)
            }

            if (uiState.termine) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bravo, ${uiState.score} bulles éclatées ! 🎉", style = MaterialTheme.typography.headlineMedium)
                    if (credits > 0) {
                        Button(onClick = { viewModel.rejouer() }, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Rejouer ($credits partie${if (credits > 1) "s" else ""} restante${if (credits > 1) "s" else ""})")
                        }
                    } else {
                        Text(
                            "Tu as utilisé tes parties ! Continue à faire des exercices pour en débloquer d'autres.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    uiState.bulles.forEach { bulle ->
                        key(bulle.id) {
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (maxWidth - TAILLE_BULLE) * bulle.x,
                                        y = (maxHeight - TAILLE_BULLE) * bulle.y
                                    )
                                    .size(TAILLE_BULLE)
                                    .background(couleursBulles[bulle.couleur].copy(alpha = 0.8f), CircleShape)
                                    .clickable { viewModel.eclater(bulle.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
