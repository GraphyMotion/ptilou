package com.ptilou.app.domain.generator

import com.ptilou.app.domain.model.CompetenceEcriture
import com.ptilou.app.domain.model.ExerciceEcriture

// Banque statique de mots à dicter (rédigée à la main, comme pour la lecture) : la
// narration lit le mot à voix haute, l'enfant l'écrit sans jamais le voir affiché.
object GenerateurExerciceEcriture {

    private var dernierMotFrequent: String? = null
    private var dernierMotInvariable: String? = null

    fun genererPour(competence: CompetenceEcriture): ExerciceEcriture {
        val mot = when (competence) {
            CompetenceEcriture.DICTEE_MOTS_FREQUENTS ->
                tirerSansRepeter(motsFrequents, dernierMotFrequent).also { dernierMotFrequent = it }
            CompetenceEcriture.DICTEE_MOTS_INVARIABLES ->
                tirerSansRepeter(motsInvariables, dernierMotInvariable).also { dernierMotInvariable = it }
        }
        return ExerciceEcriture(competence, mot)
    }

    private val motsFrequents = listOf(
        "chat", "chien", "maison", "école", "table", "chaise", "fenêtre", "jardin",
        "soleil", "lune", "arbre", "fleur", "livre", "crayon", "cahier", "ballon",
        "vélo", "voiture", "bateau", "oiseau",
        // Pas de mots avec « œ » : trop difficile à taper au clavier pour un enfant de CE1.
        "lapin", "pomme", "porte", "lit", "main", "pied", "tête", "jouet",
        "gâteau", "route", "ville", "neige", "pluie", "nuage", "étoile", "poisson",
        "cheval", "mouton", "vache", "poule",
        // Noms fréquents tirés de Lexique 3.83 (lexique.org, CC BY-SA 4.0), triés à la main.
        "nuit", "jour", "bras", "rue", "eau", "ciel", "train", "mer", "vent", "feu", "nez",
        "mur", "pain", "roi", "lait", "toit", "bain", "loup", "fruit", "sac", "matin",
        "enfant", "maman", "papa", "ami", "chapeau", "manteau", "cadeau", "avion", "camion",
        "souris", "bébé", "bouton", "dessin", "chanson", "cousin", "voisin", "copain"
    )

    private val motsInvariables = listOf(
        "dans", "avec", "sur", "sous", "pour", "chez", "mais", "donc",
        "très", "alors", "aussi", "encore", "toujours", "jamais", "beaucoup",
        "puis", "ensuite", "aujourd'hui", "hier", "demain",
        "devant", "derrière", "loin", "près", "vite", "bien", "après", "avant",
        "souvent", "maintenant", "comme", "quand"
    )
}
