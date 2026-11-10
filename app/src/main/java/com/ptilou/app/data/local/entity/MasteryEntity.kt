package com.ptilou.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mastery")
data class MasteryEntity(
    @PrimaryKey val skillCode: String,
    val score: Float = 0f,
    val attempts: Int = 0,
    val correct: Int = 0,
    val lastSeen: Long = 0L,
    val palier: Int = 0
)
