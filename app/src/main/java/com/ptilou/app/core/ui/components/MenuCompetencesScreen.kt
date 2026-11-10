package com.ptilou.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ptilou.app.core.ui.model.CompetenceUi
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouSurface

// Écran générique "menu de compétences" partagé par tous les modules (calcul, lecture, ...)
@Composable
fun MenuCompetencesScreen(
    competences: List<CompetenceUi>,
    onCompetenceChoisie: (String) -> Unit,
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    pieces: Int = 0
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = pieces)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(competences, key = { it.code }) { item ->
                    CarteCompetence(
                        libelle = item.libelle,
                        maitrise = item.maitrise,
                        onClick = { onCompetenceChoisie(item.code) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CarteCompetence(libelle: String, maitrise: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (maitrise) PtilouGreen.copy(alpha = 0.25f) else PtilouSurface,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = libelle, style = MaterialTheme.typography.titleLarge)
            if (maitrise) {
                Text(text = "Maîtrisé !", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
