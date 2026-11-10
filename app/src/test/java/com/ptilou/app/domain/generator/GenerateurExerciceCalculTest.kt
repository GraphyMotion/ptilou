package com.ptilou.app.domain.generator

import com.ptilou.app.domain.model.CompetenceCalcul
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Une réponse attendue fausse = l'enfant est corrigé à tort : on vérifie les invariants de
// chaque générateur sur de nombreux tirages aléatoires, pour chaque niveau.
class GenerateurExerciceCalculTest {

    @Test
    fun exercicesCoherentsPourToutesCompetencesEtNiveaux() {
        repeat(2000) {
            for (palier in 0..2) for (competence in CompetenceCalcul.entries) {
                val e = GenerateurExerciceCalcul.genererPour(competence, palier)
                assertTrue("réponse négative : $e", e.reponseAttendue >= 0)

                val a = e.operandeA ?: continue
                val b = e.operandeB!!
                val attendu = if (e.operateur == '+') a + b else a - b
                assertEquals("calcul posé faux : $e", attendu, e.reponseAttendue)

                val retenue = if (e.operateur == '+') a % 10 + b % 10 >= 10 else b % 10 > a % 10
                when (competence) {
                    CompetenceCalcul.ADDITION_SANS_RETENUE,
                    CompetenceCalcul.SOUSTRACTION_SANS_RETENUE -> assertFalse("retenue inattendue : $e", retenue)
                    CompetenceCalcul.ADDITION_AVEC_RETENUE,
                    CompetenceCalcul.SOUSTRACTION_AVEC_RETENUE -> assertTrue("retenue manquante : $e", retenue)
                    else -> Unit
                }
            }
        }
    }

    @Test
    fun divisionToujoursExacte() {
        repeat(2000) {
            for (palier in 0..2) {
                val e = GenerateurExerciceCalcul.genererPour(CompetenceCalcul.DIVISION, palier)
                val (dividende, diviseur) = e.enonce.removeSuffix(" = ?").split(" ÷ ").map { it.toInt() }
                assertEquals("division fausse : $e", dividende, diviseur * e.reponseAttendue)
            }
        }
    }
}
