package com.ptilou.app.domain.model

// Catalogue fixe des compétences "Découverte du monde" CE1 (module optionnel du programme).
// Le code sert de clé stable en base (table mastery, partagée avec les autres modules).
enum class CompetenceDecouverte(val code: String, val libelle: String) {
    JOURS_ET_MOIS("jours_et_mois", "Les jours et les mois"),
    SAISONS("saisons", "Les saisons"),
    CORPS_ET_SENS("corps_et_sens", "Le corps et les sens"),
    ANIMAUX_ET_VIVANT("animaux_et_vivant", "Les animaux et le vivant");

    companion object {
        fun parCode(code: String): CompetenceDecouverte = entries.first { it.code == code }
    }
}
