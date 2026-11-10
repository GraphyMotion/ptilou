package com.ptilou.app.domain.model

// Catalogue fixe des compétences écriture CE1. Le code sert de clé stable en base
// (table mastery, partagée avec les autres modules) : ne jamais le changer.
enum class CompetenceEcriture(val code: String, val libelle: String) {
    DICTEE_MOTS_FREQUENTS("dictee_mots_frequents", "Dictée de mots"),
    DICTEE_MOTS_INVARIABLES("dictee_mots_invariables", "Dictée de mots-outils");

    companion object {
        fun parCode(code: String): CompetenceEcriture = entries.first { it.code == code }
    }
}
