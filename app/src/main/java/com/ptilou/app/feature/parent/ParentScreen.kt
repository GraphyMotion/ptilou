package com.ptilou.app.feature.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouOrange
import com.ptilou.app.domain.ReglesJeu

@Composable
fun ParentScreen(
    onRetourAccueil: () -> Unit,
    onRetourArriere: () -> Unit,
    viewModel: ParentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val maj by viewModel.maj.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarreSuperieure(onRetourArriere = onRetourArriere, onRetourAccueil = onRetourAccueil, pieces = uiState.pieces)

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Espace parent", style = MaterialTheme.typography.headlineMedium)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Version ${viewModel.versionActuelle} · " + when (val e = maj) {
                            EtatMaj.Verification -> "recherche de mise à jour…"
                            EtatMaj.AJour -> "à jour ✅"
                            is EtatMaj.Disponible -> "version ${e.nouveaute.version} disponible"
                            is EtatMaj.Telechargement -> "téléchargement ${(e.progres * 100).toInt()} %"
                            EtatMaj.AutorisationRequise -> "autorise Ptilou à installer des applis, puis réappuie"
                            EtatMaj.Pret -> "installation lancée"
                            is EtatMaj.Erreur -> e.message
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    when (maj) {
                        is EtatMaj.Disponible, EtatMaj.AutorisationRequise, EtatMaj.Pret ->
                            Button(onClick = { viewModel.installerMaj() }, modifier = Modifier.padding(start = 16.dp)) {
                                Text("Installer")
                            }
                        is EtatMaj.Erreur, EtatMaj.AJour ->
                            TextButton(onClick = { viewModel.verifierMaj() }) { Text("Vérifier") }
                        else -> Unit
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Temps restant aujourd'hui : ${uiState.minutesRestantes.coerceAtLeast(0)} min " +
                            "(limite ${ReglesJeu.MINUTES_PAR_JOUR} min)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(onClick = { viewModel.accorderBonus() }, modifier = Modifier.padding(start = 16.dp)) {
                        Text("+${ReglesJeu.MINUTES_BONUS} min")
                    }
                }
                Text(
                    "Compétences, de la plus fragile à la plus solide :",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.lignes, key = { it.libelle }) { ligne ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(ligne.libelle, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = if (ligne.tentatives == 0) "jamais pratiquée"
                            else "${ligne.tentatives} essais · ${ligne.tauxReussite} % réussis",
                            color = when {
                                ligne.tentatives == 0 -> MaterialTheme.colorScheme.onSurface
                                ligne.tauxReussite < 60 -> PtilouOrange
                                else -> PtilouGreen
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
