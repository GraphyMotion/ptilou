package com.ptilou.app.feature.calcul

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val LARGEUR_COLONNE_DP = 48
private const val LARGEUR_OPERATEUR_DP = 40

// Représentation "posée" d'une addition/soustraction, comme apprise à l'école : les
// nombres alignés en colonnes par unité/dizaine/centaine, un chiffre à écrire par colonne.
// Simplification assumée : pas de case dédiée à la retenue (l'enfant la gère mentalement
// ou sur une ardoise à côté), seule la réponse finale colonne par colonne est vérifiée.
@Composable
fun CalculPose(
    operandeA: Int,
    operandeB: Int,
    operateur: Char,
    reponseAttendue: Int,
    chiffresSaisis: List<String>,
    onChiffreChange: (Int, String) -> Unit,
    onValider: () -> Unit,
    enabled: Boolean
) {
    val colonnes = maxOf(
        operandeA.toString().length,
        operandeB.toString().length,
        reponseAttendue.toString().length
    )
    val chiffresA = alignerChiffres(operandeA, colonnes)
    val chiffresB = alignerChiffres(operandeB, colonnes)

    // Comme à l'école : on commence par les unités (à droite), puis le curseur recule
    // d'une colonne à chaque chiffre écrit.
    val focus = remember(colonnes) { List(colonnes) { FocusRequester() } }
    // Aussi quand les cases sont vidées (2e chance) : retour sur les unités.
    val toutVide = chiffresSaisis.all { it.isEmpty() }
    LaunchedEffect(operandeA, operandeB, enabled, toutVide) {
        if (enabled && toutVide) focus.last().requestFocus()
    }

    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.End) {
        Row {
            Box(modifier = Modifier.width(LARGEUR_OPERATEUR_DP.dp))
            LigneChiffres(chiffresA)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(LARGEUR_OPERATEUR_DP.dp), contentAlignment = Alignment.Center) {
                Text(operateur.toString(), style = MaterialTheme.typography.headlineMedium)
            }
            LigneChiffres(chiffresB)
        }
        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.width((LARGEUR_OPERATEUR_DP + colonnes * LARGEUR_COLONNE_DP).dp)
        )
        Row {
            Box(modifier = Modifier.width(LARGEUR_OPERATEUR_DP.dp))
            repeat(colonnes) { index ->
                OutlinedTextField(
                    value = chiffresSaisis.getOrElse(index) { "" },
                    onValueChange = { valeur ->
                        val chiffre = valeur.filter(Char::isDigit).take(1)
                        onChiffreChange(index, chiffre)
                        if (chiffre.isNotEmpty() && index > 0) focus[index - 1].requestFocus()
                    },
                    enabled = enabled,
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.headlineSmall.fontSize),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onValider() }),
                    modifier = Modifier
                        .width(LARGEUR_COLONNE_DP.dp)
                        .focusRequester(focus[index])
                        .onPreviewKeyEvent { event ->
                            // Touche Entrée d'un clavier externe (comme la saisie classique).
                            val entree = event.key == Key.Enter || event.key == Key.NumPadEnter
                            if (entree && event.type == KeyEventType.KeyUp) onValider()
                            entree
                        }
                )
            }
        }
    }
}

@Composable
private fun LigneChiffres(chiffres: List<String>) {
    Row {
        chiffres.forEach { chiffre ->
            Box(modifier = Modifier.width(LARGEUR_COLONNE_DP.dp), contentAlignment = Alignment.Center) {
                Text(chiffre, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

private fun alignerChiffres(nombre: Int, colonnes: Int): List<String> =
    nombre.toString().padStart(colonnes, ' ').map { if (it == ' ') "" else it.toString() }
