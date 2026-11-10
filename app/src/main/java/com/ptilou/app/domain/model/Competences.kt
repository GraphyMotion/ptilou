package com.ptilou.app.domain.model

// Toutes les compétences de tous les modules (code, libellé), pour les écrans transverses
// (Ma progression, Espace parent).
val TOUTES_LES_COMPETENCES: List<Pair<String, String>> =
    CompetenceCalcul.entries.map { it.code to it.libelle } +
        CompetenceLecture.entries.map { it.code to it.libelle } +
        CompetenceEcriture.entries.map { it.code to it.libelle } +
        CompetenceDecouverte.entries.map { it.code to it.libelle }
