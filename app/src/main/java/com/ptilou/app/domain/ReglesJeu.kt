package com.ptilou.app.domain

// Chaque palier de points gagnés dans la journée débloque un nombre limité de parties —
// l'enfant doit continuer à faire des exercices pour en débloquer d'autres, plutôt que
// de jouer sans limite une fois le premier seuil atteint. Les points doivent en plus
// provenir d'au moins 2 thèmes différents : sinon un enfant qui ne fait que le thème
// qu'il maîtrise déjà pourrait débloquer le jeu sans jamais se diversifier.
object ReglesJeu {
    const val SEUIL_JEU_QUOTIDIEN = 10
    const val PARTIES_PAR_PALIER = 2
    const val THEMES_DISTINCTS_REQUIS = 2

    // Temps d'écran quotidien ; le parent peut accorder du temps en plus (porte parentale).
    const val MINUTES_PAR_JOUR = 30
    const val MINUTES_BONUS = 15
}
