package com.ptilou.app.core.ui.model

// Modèle générique partagé par tous les modules (calcul, lecture, ...) : chaque module
// mappe son enum de compétences (code + libellé) vers ce type pour l'affichage.
data class CompetenceUi(
    val code: String,
    val libelle: String,
    val score: Float,
    val maitrise: Boolean
)
