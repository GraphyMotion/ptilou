package com.ptilou.app.domain

// Moyenne mobile exponentielle + paliers de difficulté, repris du pattern JaneOS/Bloom
// (alpha=0.25, seuil de maîtrise=0.85) et adaptés : promotion de palier conservative,
// conditionnée au score ET à un nombre minimal de tentatives pour éviter qu'un coup
// de chance fasse sauter un niveau.
object MasteryCalculator {
    const val ALPHA = 0.25f
    const val SEUIL_MAITRISE = 0.85f

    fun miseAJourScore(scoreActuel: Float, correct: Boolean): Float {
        val cible = if (correct) 1f else 0f
        return scoreActuel + ALPHA * (cible - scoreActuel)
    }

    fun calculerPalier(score: Float, tentatives: Int): Int = when {
        score >= 0.85f && tentatives >= 60 -> 3
        score >= 0.85f && tentatives >= 30 -> 2
        score >= 0.80f && tentatives >= 12 -> 1
        else -> 0
    }
}
