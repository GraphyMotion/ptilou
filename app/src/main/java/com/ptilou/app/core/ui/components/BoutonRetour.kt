package com.ptilou.app.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Retour à l'étape précédente (ex : après avoir joué en "Facile", revenir choisir un
// autre niveau) — indispensable maintenant que la barre système est masquée en plein écran.
@Composable
fun BoutonRetour(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 16.dp, start = 4.dp)
            .size(56.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Retour",
            modifier = Modifier.size(38.dp)
        )
    }
}
