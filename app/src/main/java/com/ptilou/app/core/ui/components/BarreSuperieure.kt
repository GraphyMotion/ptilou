package com.ptilou.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Barre du haut partagée par tous les écrans : retour arrière + retour à l'accueil à
// gauche, total de pièces gagnées à droite — visible partout, pas seulement dans la
// garde-robe. Indispensable en plein écran immersif (plus de barre système pour revenir).
@Composable
fun BarreSuperieure(
    onRetourArriere: () -> Unit,
    onRetourAccueil: () -> Unit,
    pieces: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            BoutonRetour(onClick = onRetourArriere)
            BoutonAccueil(onClick = onRetourAccueil)
        }
        Text(
            text = "🪙 $pieces",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 16.dp, end = 16.dp)
        )
    }
}
