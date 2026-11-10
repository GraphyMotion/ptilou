package com.ptilou.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accessoires_possedes")
data class AccessoirePossedeEntity(
    @PrimaryKey val accessoireId: String,
    val equipe: Boolean = false
)
