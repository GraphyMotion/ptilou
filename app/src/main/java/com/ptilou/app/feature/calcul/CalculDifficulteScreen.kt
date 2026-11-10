package com.ptilou.app.feature.calcul

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
import com.ptilou.app.domain.model.CompetenceCalcul

@Composable
fun CalculDifficulteScreen(
    skillCode: String,
    onNiveauChoisi: (Int) -> Unit,
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: CalculDifficulteViewModel = hiltViewModel()
) {
    val competence = CompetenceCalcul.parCode(skillCode)
    val pieces by viewModel.pieces.collectAsState()

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
                Text(text = competence.libelle, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Choisis ton niveau",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onNiveauChoisi(0) }) { Text("Facile · 🪙 x1") }
                    Button(onClick = { onNiveauChoisi(1) }) { Text("Moyen · 🪙 x2") }
                    Button(onClick = { onNiveauChoisi(2) }) { Text("Difficile · 🪙 x3") }
                }
            }
        }
    }
}
