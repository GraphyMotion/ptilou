package com.ptilou.app.feature.lecture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.BarreSuperieure
import com.ptilou.app.core.ui.components.ConfettiOverlay
import com.ptilou.app.core.ui.components.JaugeSession
import com.ptilou.app.core.ui.components.MascotteFace
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouOrange
import com.ptilou.app.core.ui.theme.PtilouRed

@Composable
fun LectureExerciceScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: LectureExerciceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pieces by viewModel.pieces.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = pieces)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MascotteFace(humeur = uiState.humeur)

                    JaugeSession(bonnesReponses = uiState.bonnesReponsesSession, modifier = Modifier.padding(top = 8.dp))

                    if (uiState.exercice.texte.isNotBlank()) {
                        Text(
                            text = uiState.exercice.texte,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    Text(
                        text = uiState.exercice.question,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    TextButton(onClick = { viewModel.ecouter() }, modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("🔊 Écouter")
                    }

                    uiState.exercice.propositions.forEachIndexed { index, proposition ->
                        val estBonneReponse = index == uiState.exercice.bonneReponseIndex
                        val estSelectionnee = index == uiState.selectionIndex
                        val couleurBouton = when {
                            !uiState.aRepondu -> ButtonDefaults.buttonColors()
                            estBonneReponse -> ButtonDefaults.buttonColors(containerColor = PtilouGreen)
                            estSelectionnee -> ButtonDefaults.buttonColors(containerColor = PtilouRed)
                            else -> ButtonDefaults.buttonColors()
                        }
                        Button(
                            onClick = { viewModel.choisir(index) },
                            enabled = !uiState.aRepondu,
                            colors = couleurBouton,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth(0.85f)
                        ) {
                            Text(proposition, style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    if (uiState.messageReessai != null) {
                        Text(
                            text = uiState.messageReessai!!,
                            color = PtilouOrange,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    if (uiState.aRepondu) {
                        Button(
                            onClick = { viewModel.exerciceSuivant() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Exercice suivant")
                        }
                    }
                }
            }

            ConfettiOverlay(
                declenche = uiState.celebrationDeclenchee,
                onTermine = { viewModel.celebrationTerminee() }
            )
        }
    }
}
