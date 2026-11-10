package com.ptilou.app.domain.model

// Catalogue fixe des compétences lecture CE1. Le code sert de clé stable en base
// (table mastery, partagée avec le module calcul) : ne jamais le changer.
enum class CompetenceLecture(val code: String, val libelle: String) {
    RECONNAISSANCE_SYLLABES("reconnaissance_syllabes", "Compter les syllabes"),
    RECONNAISSANCE_MOTS("reconnaissance_mots", "Reconnaître les mots"),
    COMPREHENSION_PHRASES("comprehension_phrases", "Comprendre des phrases");

    companion object {
        fun parCode(code: String): CompetenceLecture = entries.first { it.code == code }
    }
}
