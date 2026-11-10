package com.ptilou.app.feature.ecriture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
fun EcritureExerciceScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: EcritureExerciceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pieces by viewModel.pieces.collectAsState()
    var saisie by remember(uiState.exercice, uiState.tentativesEchouees) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    fun validerSaisie() {
        viewModel.valider(saisie)
    }

    LaunchedEffect(uiState.exercice, uiState.tentativesEchouees) {
        focusRequester.requestFocus()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().imePadding()) {
                BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = pieces)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MascotteFace(humeur = uiState.humeur)

                    JaugeSession(bonnesReponses = uiState.bonnesReponsesSession, modifier = Modifier.padding(top = 8.dp))

                    Text(
                        text = "Écoute bien le mot et écris-le.",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    TextButton(onClick = { viewModel.ecouter() }) {
                        Text("🔊 Écouter le mot")
                    }

                    OutlinedTextField(
                        value = saisie,
                        onValueChange = { saisie = it },
                        enabled = !uiState.aRepondu,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { validerSaisie() }),
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .width(220.dp)
                            .focusRequester(focusRequester)
                            .onPreviewKeyEvent { event ->
                                if (event.type == KeyEventType.KeyUp &&
                                    (event.key == Key.Enter || event.key == Key.NumPadEnter)
                                ) {
                                    validerSaisie()
                                    true
                                } else {
                                    false
                                }
                            }
                    )

                    if (uiState.messageReessai != null) {
                        Text(
                            text = uiState.messageReessai!!,
                            color = PtilouOrange,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    if (uiState.aRepondu) {
                        val correct = uiState.dernierResultatCorrect == true
                        Text(
                            text = if (correct) "Bravo, c'est ça !" else "Pas tout à fait, le mot était : ${uiState.exercice.motAEcrire}",
                            color = if (correct) PtilouGreen else PtilouRed,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.exerciceSuivant() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Exercice suivant")
                        }
                    } else {
                        Button(
                            onClick = { validerSaisie() },
                            enabled = saisie.isNotBlank(),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Valider")
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
