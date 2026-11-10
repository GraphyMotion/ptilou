package com.ptilou.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "economie")
data class EconomieEntity(
    @PrimaryKey val id: Int = 1,
    val pieces: Int = 0,
    val pointsAujourdhui: Int = 0,
    val dernierJourPointsEpoch: Long = 0L,
    val partiesJoueesAujourdhui: Int = 0,
    val creditsGagnesAujourdhui: Int = 0,
    // Compteurs remis à zéro à chaque palier débloqué (pas seulement chaque jour) : servent
    // à vérifier que les 10 points du palier viennent d'au moins 2 thèmes différents.
    val pointsCalculCycle: Int = 0,
    val pointsLectureCycle: Int = 0,
    val pointsEcritureCycle: Int = 0,
    val pointsDecouverteCycle: Int = 0
)
