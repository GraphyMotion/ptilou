package com.ptilou.app.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun JaugeSession(bonnesReponses: Int, modifier: Modifier = Modifier) {
    val etoilesRemplies = if (bonnesReponses == 0) 0 else ((bonnesReponses - 1) % 5) + 1
    Row(modifier = modifier) {
        repeat(5) { index ->
            Text(
                text = if (index < etoilesRemplies) "⭐" else "☆",
                fontSize = 28.sp
            )
        }
    }
}
