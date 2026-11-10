package com.ptilou.app.domain.model

// Où l'accessoire se positionne sur/autour de la mascotte — permet à l'UI de placer
// chaque emoji correctement (un chapeau ne se porte pas au même endroit que des lunettes).
enum class PositionAccessoire { TETE, VISAGE, COU, OBJET }

// Catalogue fixe des accessoires de la garde-robe de Ptilou, du moins cher au plus cher.
// Le id sert de clé stable en base : ne jamais le changer une fois des données enregistrées.
enum class Accessoire(
    val id: String,
    val emoji: String,
    val libelle: String,
    val cout: Int,
    val position: PositionAccessoire
) {
    LUNETTES("lunettes", "🕶️", "Lunettes", 10, PositionAccessoire.VISAGE),
    CHAPEAU("chapeau", "🎩", "Chapeau", 15, PositionAccessoire.TETE),
    NOEUD("noeud", "🎀", "Nœud papillon", 20, PositionAccessoire.COU),
    COURONNE("couronne", "👑", "Couronne", 30, PositionAccessoire.TETE),
    CAPE("cape", "🦸", "Cape de super-héros", 50, PositionAccessoire.OBJET),
    BAGUETTE_MAGIQUE("baguette_magique", "🪄", "Baguette magique", 70, PositionAccessoire.OBJET),
    MEDAILLE_CHAMPION("medaille_champion", "🥇", "Médaille de champion", 90, PositionAccessoire.OBJET),
    TROPHEE("trophee", "🏆", "Trophée doré", 120, PositionAccessoire.OBJET),
    ETOILE_FILANTE("etoile_filante", "🌟", "Étoile filante", 150, PositionAccessoire.OBJET),
    AMI_LICORNE("ami_licorne", "🦄", "Ami licorne", 200, PositionAccessoire.OBJET),
    BOULE_CRISTAL("boule_cristal", "🔮", "Boule de cristal", 250, PositionAccessoire.OBJET),
    DRAGON("dragon", "🐉", "Dragon magique", 320, PositionAccessoire.OBJET),
    CHATEAU_MAGIQUE("chateau_magique", "🏰", "Château magique", 400, PositionAccessoire.OBJET),
    FUSEE("fusee", "🚀", "Fusée", 500, PositionAccessoire.OBJET),
    PLANETE("planete", "🪐", "Planète lointaine", 600, PositionAccessoire.OBJET),
    COURONNE_DIAMANT("couronne_diamant", "💎", "Couronne en diamant", 750, PositionAccessoire.TETE);

    companion object {
        fun parId(id: String): Accessoire = entries.first { it.id == id }
    }
}
