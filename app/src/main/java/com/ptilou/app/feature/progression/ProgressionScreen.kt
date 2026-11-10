package com.ptilou.app.feature.progression

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.BarreSuperieure
import com.ptilou.app.core.ui.components.PorteParentale
import com.ptilou.app.core.ui.model.CompetenceUi
import com.ptilou.app.core.ui.theme.PtilouOrange

@Composable
fun ProgressionScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    onOuvrirParent: () -> Unit,
    viewModel: ProgressionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var porteOuverte by remember { mutableStateOf(false) }

    if (porteOuverte) {
        PorteParentale(
            onOuvert = { porteOuverte = false; onOuvrirParent() },
            onAnnuler = { porteOuverte = false }
        )
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = uiState.pieces)

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "Ma progression", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "🔥 ${uiState.streak.streakActuel} jour(s) d'affilée (record : ${uiState.streak.streakMax})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "🏅 ${uiState.nombreBadges} badge(s) obtenu(s)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                TextButton(onClick = { porteOuverte = true }, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text("👨‍👩‍👦 Espace parent")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.competences, key = { it.code }) { item ->
                    LigneCompetence(item)
                }
            }
        }
    }
}

@Composable
private fun LigneCompetence(item: CompetenceUi) {
    Surface(shape = RoundedCornerShape(16.dp), shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.libelle, style = MaterialTheme.typography.titleLarge)
            LinearProgressIndicator(
                progress = { item.score.coerceIn(0f, 1f) },
                color = PtilouOrange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            if (item.maitrise) {
                Text(text = "Maîtrisé !", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
