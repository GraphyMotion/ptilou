package com.ptilou.app.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.ptilou.app.core.ui.theme.PtilouBlue
import com.ptilou.app.core.ui.theme.PtilouGreen
import com.ptilou.app.core.ui.theme.PtilouOrange
import com.ptilou.app.core.ui.theme.PtilouRed
import com.ptilou.app.core.ui.theme.PtilouYellow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particule(val dx: Float, val dy: Float, val couleur: Color) {
    companion object {
        fun aleatoire(): Particule {
            val angle = Random.nextFloat() * (2 * Math.PI).toFloat()
            val vitesse = 0.3f + Random.nextFloat() * 0.35f
            return Particule(
                dx = cos(angle) * vitesse,
                dy = sin(angle) * vitesse,
                couleur = listOf(PtilouOrange, PtilouBlue, PtilouGreen, PtilouYellow, PtilouRed).random()
            )
        }
    }
}

// Petite explosion de confettis déclenchée une fois par [declenche], sans dépendance externe
// (Lottie, etc.) — suffisant pour un effet de célébration ponctuel.
@Composable
fun ConfettiOverlay(declenche: Boolean, onTermine: () -> Unit) {
    if (!declenche) return

    val particules = remember(declenche) { List(18) { Particule.aleatoire() } }
    val progression = remember(declenche) { Animatable(0f) }

    LaunchedEffect(declenche) {
        progression.snapTo(0f)
        progression.animateTo(1f, animationSpec = tween(durationMillis = 900))
        onTermine()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centre = Offset(size.width / 2, size.height / 2)
        val t = progression.value
        particules.forEach { particule ->
            val x = centre.x + particule.dx * t * size.minDimension
            val y = centre.y + particule.dy * t * size.minDimension
            drawCircle(color = particule.couleur, radius = 10f * (1f - t), center = Offset(x, y))
        }
    }
}
