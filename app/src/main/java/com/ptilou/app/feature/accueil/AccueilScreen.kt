package com.ptilou.app.feature.accueil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptilou.app.core.ui.components.HumeurPtilou
import com.ptilou.app.core.ui.components.MascotteFace
import com.ptilou.app.core.ui.components.tailleMascotteAdaptee
import com.ptilou.app.domain.ReglesJeu

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccueilScreen(
    onOuvrirCalcul: () -> Unit = {},
    onOuvrirLecture: () -> Unit = {},
    onOuvrirEcriture: () -> Unit = {},
    onOuvrirDecouverte: () -> Unit = {},
    onOuvrirProgression: () -> Unit = {},
    onOuvrirGarderobe: () -> Unit = {},
    onOuvrirJeu: () -> Unit = {},
    viewModel: AccueilViewModel = hiltViewModel()
) {
    val accessoireEquipe by viewModel.accessoireEquipe.collectAsState()
    val voixFrancaiseDisponible by viewModel.voixFrancaiseDisponible.collectAsState()
    val pieces by viewModel.pieces.collectAsState()
    val prenom by viewModel.prenom.collectAsState()
    val progressionJeu by viewModel.progressionJeu.collectAsState()
    val jeuDebloque = progressionJeu.creditsDisponibles > 0

    var afficherDialoguePrenom by remember { mutableStateOf(false) }
    var brouillonPrenom by remember { mutableStateOf("") }

    LaunchedEffect(prenom) {
        if (prenom.isNullOrBlank()) {
            afficherDialoguePrenom = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "🪙 $pieces",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        )
        // Défilable pour les petits écrans (téléphone en paysage), tout en restant centré
        // verticalement sur tablette (hauteur min = celle de l'écran).
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MascotteFace(humeur = HumeurPtilou.CONTENT, accessoire = accessoireEquipe, taille = tailleMascotteAdaptee())
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!prenom.isNullOrBlank()) "Salut $prenom, je suis Ptilou !" else "Salut, je suis Ptilou !",
                    style = MaterialTheme.typography.headlineLarge
                )
                TextButton(onClick = {
                    brouillonPrenom = prenom.orEmpty()
                    afficherDialoguePrenom = true
                }) {
                    Text("✏️")
                }
            }
            FlowRow(
                modifier = Modifier.padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onOuvrirCalcul) {
                    Text("Calcul", style = MaterialTheme.typography.labelLarge)
                }
                Button(onClick = onOuvrirLecture) {
                    Text("Lecture", style = MaterialTheme.typography.labelLarge)
                }
                Button(onClick = onOuvrirEcriture) {
                    Text("Écriture", style = MaterialTheme.typography.labelLarge)
                }
                Button(onClick = onOuvrirDecouverte) {
                    Text("Découverte", style = MaterialTheme.typography.labelLarge)
                }
            }
            OutlinedButton(onClick = onOuvrirProgression, modifier = Modifier.padding(top = 16.dp)) {
                Text("Ma progression", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = onOuvrirGarderobe, modifier = Modifier.padding(top = 8.dp)) {
                Text("Garde-robe de Ptilou", style = MaterialTheme.typography.labelLarge)
            }
            Button(
                onClick = onOuvrirJeu,
                enabled = jeuDebloque,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = when {
                        jeuDebloque -> {
                            val n = progressionJeu.creditsDisponibles
                            "🎮 Jeu de Ptilou ($n partie${if (n > 1) "s" else ""})"
                        }
                        progressionJeu.pointsCycleActuel >= ReglesJeu.SEUIL_JEU_QUOTIDIEN &&
                            progressionJeu.themesDistinctsCycleActuel < ReglesJeu.THEMES_DISTINCTS_REQUIS -> {
                            "🔒 Jeu de Ptilou (essaie un autre thème !)"
                        }
                        else -> {
                            "🔒 Jeu de Ptilou (${progressionJeu.pointsCycleActuel}/${ReglesJeu.SEUIL_JEU_QUOTIDIEN} points, 2 thèmes)"
                        }
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (!voixFrancaiseDisponible) {
                TextButton(
                    onClick = { viewModel.installerVoixFrancaise() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("🔊 Installer la voix française", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        }
        }

        if (afficherDialoguePrenom) {
            AlertDialog(
                onDismissRequest = {
                    if (!prenom.isNullOrBlank()) afficherDialoguePrenom = false
                },
                title = { Text("Comment tu t'appelles ?") },
                text = {
                    OutlinedTextField(
                        value = brouillonPrenom,
                        onValueChange = { brouillonPrenom = it },
                        singleLine = true,
                        label = { Text("Prénom") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (brouillonPrenom.isNotBlank()) {
                                viewModel.enregistrerPrenom(brouillonPrenom.trim())
                                afficherDialoguePrenom = false
                            }
                        },
                        enabled = brouillonPrenom.isNotBlank()
                    ) {
                        Text("Valider")
                    }
                }
            )
        }
    }
}
