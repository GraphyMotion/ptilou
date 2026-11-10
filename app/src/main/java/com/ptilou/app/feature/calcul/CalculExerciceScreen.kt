package com.ptilou.app.feature.calcul

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
fun CalculExerciceScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: CalculExerciceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pieces by viewModel.pieces.collectAsState()
    val exercice = uiState.exercice
    val enPose = exercice.operandeA != null && exercice.operandeB != null && exercice.operateur != null

    var saisie by remember(exercice, uiState.tentativesEchouees) { mutableStateOf("") }
    val colonnesPose = remember(exercice) {
        if (enPose) {
            maxOf(exercice.operandeA!!.toString().length, exercice.operandeB!!.toString().length, exercice.reponseAttendue.toString().length)
        } else 0
    }
    var chiffresSaisis by remember(exercice, uiState.tentativesEchouees) { mutableStateOf(List(colonnesPose) { "" }) }
    val focusRequester = remember { FocusRequester() }

    fun validerSaisie() {
        if (enPose) {
            if (chiffresSaisis.all { it.isNotBlank() }) {
                chiffresSaisis.joinToString("").toIntOrNull()?.let(viewModel::valider)
            }
        } else {
            saisie.toIntOrNull()?.let(viewModel::valider)
        }
    }

    LaunchedEffect(exercice, uiState.tentativesEchouees) {
        if (!enPose) focusRequester.requestFocus()
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

                    if (!enPose) {
                        Text(
                            text = exercice.enonce,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    TextButton(onClick = { viewModel.ecouter() }) {
                        Text("🔊 Écouter")
                    }

                    if (enPose) {
                        CalculPose(
                            operandeA = exercice.operandeA!!,
                            operandeB = exercice.operandeB!!,
                            operateur = exercice.operateur!!,
                            reponseAttendue = exercice.reponseAttendue,
                            chiffresSaisis = chiffresSaisis,
                            onChiffreChange = { index, valeur ->
                                chiffresSaisis = chiffresSaisis.toMutableList().also { it[index] = valeur }
                            },
                            enabled = !uiState.aRepondu
                        )
                    } else {
                        OutlinedTextField(
                            value = saisie,
                            onValueChange = { saisie = it.filter(Char::isDigit) },
                            enabled = !uiState.aRepondu,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { validerSaisie() }),
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .width(160.dp)
                                .focusRequester(focusRequester)
                                .onPreviewKeyEvent { event ->
                                    // Prise en charge d'un clavier externe (Bluetooth/USB) : la touche
                                    // Entrée du pavé numérique ne déclenche pas toujours l'action IME.
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
                        val correct = uiState.dernierResultatCorrect == true
                        Text(
                            text = if (correct) "Bravo, c'est ça !" else "Pas tout à fait, la réponse était ${uiState.exercice.reponseAttendue}.",
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
                        val peutValider = if (enPose) chiffresSaisis.all { it.isNotBlank() } else saisie.isNotBlank()
                        Button(
                            onClick = { validerSaisie() },
                            enabled = peutValider,
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
