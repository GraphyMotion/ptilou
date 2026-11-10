package com.ptilou.app.domain.generator

import com.ptilou.app.domain.model.CompetenceCalcul
import com.ptilou.app.domain.model.ExerciceCalcul
import kotlin.random.Random

// Génération procédurale déterministe (pas d'IA) : gratuite, infinie, 100% juste
// mathématiquement, fonctionne hors-ligne. Calibrée sur les repères Eduscol CE1.
object GenerateurExerciceCalcul {

    fun genererPour(competence: CompetenceCalcul, palier: Int): ExerciceCalcul = when (competence) {
        CompetenceCalcul.ADDITION_SANS_RETENUE -> additionSansRetenue(palier)
        CompetenceCalcul.ADDITION_AVEC_RETENUE -> additionAvecRetenue(palier)
        CompetenceCalcul.SOUSTRACTION_SANS_RETENUE -> soustractionSansRetenue(palier)
        CompetenceCalcul.SOUSTRACTION_AVEC_RETENUE -> soustractionAvecRetenue(palier)
        CompetenceCalcul.TABLES_MULTIPLICATION -> tableMultiplication(palier)
        CompetenceCalcul.DIVISION -> division(palier)
        CompetenceCalcul.PROBLEMES_UNE_ETAPE -> problemeUneEtape(palier)
        CompetenceCalcul.COMPARAISON_NOMBRES -> comparaisonNombres(palier)
        CompetenceCalcul.SUITE_NUMERIQUE -> suiteNumerique(palier)
        CompetenceCalcul.NOMBRE_AVANT_APRES -> nombreAvantApres(palier)
        CompetenceCalcul.DECOMPOSITION_NOMBRES -> decompositionNombres(palier)
    }

    // Numération : jusqu'à 100 (Facile/Moyen), jusqu'à 1000 (Difficile), conforme aux
    // repères Eduscol CE1. Pas d'écriture des nombres en toutes lettres — trop de pièges
    // orthographiques en français (quatre-vingts, soixante-dix...) pour une génération fiable.
    private fun plafondNumeration(palier: Int) = when (palier) {
        0 -> 100
        1 -> 300
        else -> 1000
    }

    private fun comparaisonNombres(palier: Int): ExerciceCalcul {
        val max = plafondNumeration(palier)
        val a = Random.nextInt(1, max)
        var b: Int
        do {
            b = Random.nextInt(1, max)
        } while (b == a)
        val demanderLePlusGrand = Random.nextBoolean()
        val reponse = if (demanderLePlusGrand) maxOf(a, b) else minOf(a, b)
        val consigne = if (demanderLePlusGrand) "le plus grand" else "le plus petit"
        return ExerciceCalcul(CompetenceCalcul.COMPARAISON_NOMBRES, "Quel est $consigne nombre : $a ou $b ?", reponse)
    }

    private fun suiteNumerique(palier: Int): ExerciceCalcul {
        val max = plafondNumeration(palier)
        val pas = when {
            palier >= 2 && Random.nextBoolean() -> 100
            else -> if (Random.nextBoolean()) 10 else 1
        }
        val debut = Random.nextInt(0, (max - pas * 4).coerceAtLeast(1))
        val termes = (0..2).map { debut + it * pas }
        val reponse = debut + 3 * pas
        return ExerciceCalcul(CompetenceCalcul.SUITE_NUMERIQUE, "${termes.joinToString(", ")}, ? ", reponse)
    }

    private fun nombreAvantApres(palier: Int): ExerciceCalcul {
        val max = plafondNumeration(palier)
        val n = Random.nextInt(1, max)
        val demanderApres = Random.nextBoolean()
        return if (demanderApres) {
            ExerciceCalcul(CompetenceCalcul.NOMBRE_AVANT_APRES, "Quel nombre vient juste après $n ?", n + 1)
        } else {
            ExerciceCalcul(CompetenceCalcul.NOMBRE_AVANT_APRES, "Quel nombre vient juste avant $n ?", n - 1)
        }
    }

    private fun decompositionNombres(palier: Int): ExerciceCalcul {
        val max = plafondNumeration(palier)
        val n = Random.nextInt(10, max)
        return if (Random.nextBoolean()) {
            ExerciceCalcul(CompetenceCalcul.DECOMPOSITION_NOMBRES, "Combien de dizaines entières y a-t-il dans $n ?", n / 10)
        } else {
            ExerciceCalcul(CompetenceCalcul.DECOMPOSITION_NOMBRES, "Dans $n, combien reste-t-il d'unités après avoir enlevé les dizaines ?", n % 10)
        }
    }

    private fun plafondAddition(palier: Int) = when (palier) {
        0 -> 20
        1 -> 50
        else -> 100
    }

    private fun additionSansRetenue(palier: Int): ExerciceCalcul {
        val max = plafondAddition(palier)
        while (true) {
            val a = Random.nextInt(1, max)
            val b = Random.nextInt(1, max - a + 1)
            if (a + b <= max && (a % 10) + (b % 10) < 10) {
                return ExerciceCalcul(CompetenceCalcul.ADDITION_SANS_RETENUE, "$a + $b = ?", a + b, a, b, '+')
            }
        }
    }

    private fun additionAvecRetenue(palier: Int): ExerciceCalcul {
        val max = plafondAddition(palier).coerceAtLeast(20)
        while (true) {
            val a = Random.nextInt(1, max)
            val b = Random.nextInt(1, max - a + 1)
            if (a + b <= max && (a % 10) + (b % 10) >= 10) {
                return ExerciceCalcul(CompetenceCalcul.ADDITION_AVEC_RETENUE, "$a + $b = ?", a + b, a, b, '+')
            }
        }
    }

    private fun soustractionSansRetenue(palier: Int): ExerciceCalcul {
        val max = plafondAddition(palier)
        while (true) {
            val a = Random.nextInt(1, max)
            val b = Random.nextInt(0, a + 1)
            if ((b % 10) <= (a % 10)) {
                return ExerciceCalcul(CompetenceCalcul.SOUSTRACTION_SANS_RETENUE, "$a - $b = ?", a - b, a, b, '-')
            }
        }
    }

    private fun soustractionAvecRetenue(palier: Int): ExerciceCalcul {
        val max = plafondAddition(palier).coerceAtLeast(20)
        while (true) {
            val a = Random.nextInt(11, max)
            val b = Random.nextInt(1, a)
            if ((b % 10) > (a % 10)) {
                return ExerciceCalcul(CompetenceCalcul.SOUSTRACTION_AVEC_RETENUE, "$a - $b = ?", a - b, a, b, '-')
            }
        }
    }

    private var derniereTableFacteur: Pair<Int, Int>? = null

    private fun tableMultiplication(palier: Int): ExerciceCalcul {
        val tablesDisponibles = when (palier) {
            0 -> listOf(2)
            1 -> listOf(2, 5)
            else -> listOf(2, 5, 10)
        }
        var table: Int
        var facteur: Int
        do {
            table = tablesDisponibles.random()
            facteur = Random.nextInt(0, 11)
        } while (table to facteur == derniereTableFacteur)
        derniereTableFacteur = table to facteur
        return ExerciceCalcul(CompetenceCalcul.TABLES_MULTIPLICATION, "$table x $facteur = ?", table * facteur)
    }

    // Division CE1 = partage exact, construite à l'envers depuis les tables déjà connues
    // (dividende = diviseur × quotient) : toujours un résultat entier, jamais de reste.
    private var derniereDivision: Pair<Int, Int>? = null

    private fun division(palier: Int): ExerciceCalcul {
        val diviseurs = when (palier) {
            0 -> listOf(2)
            1 -> listOf(2, 5)
            else -> listOf(2, 5, 10)
        }
        var diviseur: Int
        var quotient: Int
        do {
            diviseur = diviseurs.random()
            quotient = Random.nextInt(1, 11)
        } while (diviseur to quotient == derniereDivision)
        derniereDivision = diviseur to quotient
        return ExerciceCalcul(CompetenceCalcul.DIVISION, "${diviseur * quotient} ÷ $diviseur = ?", quotient)
    }

    private data class Modele(val texte: String, val soustraction: Boolean)

    private val modelesProblemes = listOf(
        Modele("Léa a %d bonbons. Sa maman lui en donne %d de plus. Combien de bonbons a-t-elle maintenant ?", false),
        Modele("Il y a %d élèves dans la cour. %d élèves de plus arrivent. Combien y a-t-il d'élèves maintenant ?", false),
        Modele("Tom a %d billes. Il en gagne %d. Combien de billes a-t-il maintenant ?", false),
        Modele("Tom a %d billes. Il en perd %d. Combien lui en reste-t-il ?", true),
        Modele("Il y a %d gâteaux sur la table. On en mange %d. Combien en reste-t-il ?", true),
        Modele("Léa avait %d images. Elle en donne %d à son frère. Combien lui en reste-t-il ?", true)
    )

    private var dernierProbleme: Modele? = null

    private fun problemeUneEtape(palier: Int): ExerciceCalcul {
        val modele = tirerSansRepeter(modelesProblemes, dernierProbleme)
        dernierProbleme = modele
        val max = plafondAddition(palier)
        val a: Int
        val b: Int
        val reponse: Int
        if (modele.soustraction) {
            a = Random.nextInt(2, max)
            b = Random.nextInt(1, a)
            reponse = a - b
        } else {
            a = Random.nextInt(1, max)
            b = Random.nextInt(1, max - a + 1)
            reponse = a + b
        }
        return ExerciceCalcul(CompetenceCalcul.PROBLEMES_UNE_ETAPE, modele.texte.format(a, b), reponse)
    }
}
