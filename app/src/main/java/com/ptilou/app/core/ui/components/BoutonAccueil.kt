package com.ptilou.app.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Bouton retour à l'accueil partagé par tous les écrans — icône plutôt que texte (plus
// lisible pour un enfant de CE1), avec statusBarsPadding() pour ne pas passer sous la
// barre de statut en edge-to-edge. Légèrement décalé du bord et agrandi pour rester
// facile à toucher pour un enfant, même dans le coin de l'écran.
@Composable
fun BoutonAccueil(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 16.dp, start = 4.dp)
            .size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Retour à l'accueil",
            modifier = Modifier.size(38.dp)
        )
    }
}
