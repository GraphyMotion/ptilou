package com.ptilou.app.feature.decouverte

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.MenuCompetencesScreen

@Composable
fun DecouverteMenuScreen(
    onCompetenceChoisie: (String) -> Unit,
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: DecouverteMenuViewModel = hiltViewModel()
) {
    val competences by viewModel.competences.collectAsState()
    val pieces by viewModel.pieces.collectAsState()
    MenuCompetencesScreen(
        competences = competences,
        onCompetenceChoisie = onCompetenceChoisie,
        onRetourAccueil = onRetourAccueil,
        onRetourArriere = onRetourArriere,
        pieces = pieces
    )
}
