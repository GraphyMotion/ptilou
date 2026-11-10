package com.ptilou.app.feature.garderobe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.BarreSuperieure
import com.ptilou.app.core.ui.components.HumeurPtilou
import com.ptilou.app.core.ui.components.MascotteFace
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouSurface
import com.ptilou.app.data.repository.EtatAccessoire

@Composable
fun GarderobeScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: GarderobeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accessoireEquipe = uiState.accessoires.firstOrNull { it.equipe }?.accessoire

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = uiState.pieces)

            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MascotteFace(humeur = HumeurPtilou.CONTENT, accessoire = accessoireEquipe)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.accessoires, key = { it.accessoire.id }) { etat ->
                    CarteAccessoire(etat, onClick = { viewModel.onClicAccessoire(etat) })
                }
            }
        }
    }
}

@Composable
private fun CarteAccessoire(etat: EtatAccessoire, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = if (etat.equipe) PtilouGreen.copy(alpha = 0.25f) else PtilouSurface,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(etat.accessoire.emoji, fontSize = 32.sp)
                Text(
                    text = etat.accessoire.libelle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Text(
                text = when {
                    etat.equipe -> "Retirer"
                    etat.possede -> "Équiper"
                    else -> "${etat.accessoire.cout} 🪙"
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
