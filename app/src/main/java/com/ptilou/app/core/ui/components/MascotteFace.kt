package com.ptilou.app.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.ptilou.app.core.ui.theme.PtilouOrange
import com.ptilou.app.domain.model.Accessoire
import com.ptilou.app.domain.model.PositionAccessoire

enum class HumeurPtilou { NEUTRE, CONTENT, REFLECHIT }

// Sur téléphone (surtout en paysage), la mascotte en taille normale pousse l'exercice hors
// de l'écran : on la réduit quand la hauteur disponible est faible.
@Composable
fun tailleMascotteAdaptee(): Dp =
    if (LocalConfiguration.current.screenHeightDp < 600) 64.dp else 140.dp

// Pas d'illustration disponible : visage dessiné en formes simples, remplaçable plus tard
// par une vraie mascotte (Image) sans changer les appels à ce composable.
@Composable
fun MascotteFace(
    humeur: HumeurPtilou = HumeurPtilou.NEUTRE,
    accessoire: Accessoire? = null,
    modifier: Modifier = Modifier,
    taille: Dp = 140.dp
) {
    // Les positions des accessoires sont réglées pour 140 dp : on les met à l'échelle.
    val f = taille.value / 140f
    Box(modifier = modifier.size(taille), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(taille)) {
            val rayon = size.minDimension / 2
            val centre = Offset(size.width / 2, size.height / 2)

            drawCircle(color = PtilouOrange, radius = rayon, center = centre)

            val decalageYeuxX = rayon * 0.4f
            val decalageYeuxY = rayon * 0.15f
            val rayonYeux = rayon * 0.1f
            drawCircle(Color.Black, rayonYeux, center = Offset(centre.x - decalageYeuxX, centre.y - decalageYeuxY))
            drawCircle(Color.Black, rayonYeux, center = Offset(centre.x + decalageYeuxX, centre.y - decalageYeuxY))

            val boucheY = centre.y + rayon * 0.3f
            val largeurBouche = rayon * 0.5f
            val chemin = Path().apply {
                when (humeur) {
                    HumeurPtilou.CONTENT -> {
                        moveTo(centre.x - largeurBouche, boucheY)
                        quadraticTo(centre.x, boucheY + rayon * 0.4f, centre.x + largeurBouche, boucheY)
                    }
                    HumeurPtilou.REFLECHIT -> {
                        moveTo(centre.x - largeurBouche * 0.6f, boucheY)
                        lineTo(centre.x + largeurBouche * 0.6f, boucheY)
                    }
                    HumeurPtilou.NEUTRE -> {
                        moveTo(centre.x - largeurBouche * 0.5f, boucheY)
                        quadraticTo(centre.x, boucheY + rayon * 0.12f, centre.x + largeurBouche * 0.5f, boucheY)
                    }
                }
            }
            drawPath(chemin, color = Color.Black, style = Stroke(width = rayon * 0.08f, cap = StrokeCap.Round))
        }

        if (accessoire != null) {
            // Chaque catégorie d'accessoire a sa zone et sa taille : la tête (chapeau/couronne),
            // le visage (lunettes, en grand pour couvrir les deux yeux dessinés), le cou (nœud
            // papillon), ou un objet tenu à côté (plus petit, décalé sur le côté).
            val (decalageX, decalageY, taillePolice) = when (accessoire.position) {
                PositionAccessoire.TETE -> Triple(0.dp, (-58).dp, 36.sp)
                PositionAccessoire.VISAGE -> Triple(0.dp, (-11).dp, 72.sp)
                PositionAccessoire.COU -> Triple(0.dp, 50.dp, 36.sp)
                PositionAccessoire.OBJET -> Triple(68.dp, 10.dp, 32.sp)
            }
            Text(
                text = accessoire.emoji,
                fontSize = taillePolice * f,
                modifier = Modifier.offset(x = decalageX * f, y = decalageY * f)
            )
        }
    }
}
