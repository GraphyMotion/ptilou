package com.ptilou.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ptilou.app.domain.ReglesJeu

// Affiché par-dessus toute l'app quand le temps du jour est écoulé. La Surface plein écran
// bloque les touches vers les écrans dessous.
@Composable
fun EcranFatigue(onAccorderBonus: () -> Unit) {
    var porteOuverte by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MascotteFace(humeur = HumeurPtilou.NEUTRE)
            Text(
                text = "Ptilou est fatigué 😴",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Tu as bien travaillé aujourd'hui. À demain !",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            TextButton(onClick = { porteOuverte = true }, modifier = Modifier.padding(top = 32.dp)) {
                Text("Parent : accorder ${ReglesJeu.MINUTES_BONUS} min de plus")
            }
        }
    }

    if (porteOuverte) {
        PorteParentale(
            onOuvert = {
                porteOuverte = false
                onAccorderBonus()
            },
            onAnnuler = { porteOuverte = false }
        )
    }
}
