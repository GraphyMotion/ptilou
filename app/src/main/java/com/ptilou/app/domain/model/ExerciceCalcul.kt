package com.ptilou.app.domain.model

data class ExerciceCalcul(
    val competence: CompetenceCalcul,
    val enonce: String,
    val reponseAttendue: Int,
    // Renseignés uniquement pour addition/soustraction : permettent d'afficher
    // l'opération "posée" en colonnes, comme apprise à l'école. Null pour les tables
    // de multiplication et les problèmes, affichés sous forme de phrase.
    val operandeA: Int? = null,
    val operandeB: Int? = null,
    val operateur: Char? = null
)
