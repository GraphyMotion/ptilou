package com.ptilou.app.feature.lecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.MenuCompetencesScreen

@Composable
fun LectureMenuScreen(
    onCompetenceChoisie: (String) -> Unit,
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: LectureMenuViewModel = hiltViewModel()
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
