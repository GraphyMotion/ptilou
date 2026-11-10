package com.ptilou.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Ligne unique (id fixe = 1) : un seul enfant utilise l'app pour l'instant
@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val id: Int = 1,
    val streakActuel: Int = 0,
    val streakMax: Int = 0,
    val dernierJourActifEpoch: Long = 0L
)
