package com.ptilou.app.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// Porte parentale classique des applis enfants : une multiplication hors de portée d'un
// CE1 (ex. 17 × 8). Pas de code PIN à mémoriser ni à stocker.
@Composable
fun PorteParentale(onOuvert: () -> Unit, onAnnuler: () -> Unit) {
    val a = remember { Random.nextInt(12, 20) }
    val b = remember { Random.nextInt(6, 10) }
    var saisie by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onAnnuler,
        title = { Text("Réservé aux parents") },
        text = {
            Column {
                Text("Pour continuer, combien font $a × $b ?")
                OutlinedTextField(
                    value = saisie,
                    onValueChange = { saisie = it.filter(Char::isDigit) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (saisie.toIntOrNull() == a * b) onOuvert() else saisie = "" }) {
                Text("Valider")
            }
        },
        dismissButton = { TextButton(onClick = onAnnuler) { Text("Annuler") } }
    )
}
