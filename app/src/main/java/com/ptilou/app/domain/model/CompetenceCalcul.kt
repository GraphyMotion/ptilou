package com.ptilou.app.domain.model

// Catalogue fixe des compétences calcul CE1 (repères Eduscol). Le code sert de clé
// stable en base (table mastery) : ne jamais le changer une fois des données enregistrées.
enum class CompetenceCalcul(val code: String, val libelle: String) {
    ADDITION_SANS_RETENUE("addition_sans_retenue", "Additions simples"),
    ADDITION_AVEC_RETENUE("addition_avec_retenue", "Additions avec retenue"),
    SOUSTRACTION_SANS_RETENUE("soustraction_sans_retenue", "Soustractions simples"),
    SOUSTRACTION_AVEC_RETENUE("soustraction_avec_retenue", "Soustractions avec retenue"),
    TABLES_MULTIPLICATION("tables_multiplication", "Tables (x2, x5, x10)"),
    DIVISION("division", "Partager (division)"),
    PROBLEMES_UNE_ETAPE("problemes_une_etape", "Petits problèmes"),
    COMPARAISON_NOMBRES("comparaison_nombres", "Comparer les nombres"),
    SUITE_NUMERIQUE("suite_numerique", "Continuer la suite"),
    NOMBRE_AVANT_APRES("nombre_avant_apres", "Juste avant, juste après"),
    DECOMPOSITION_NOMBRES("decomposition_nombres", "Décomposer un nombre");

    companion object {
        fun parCode(code: String): CompetenceCalcul = entries.first { it.code == code }
    }
}
