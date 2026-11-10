package com.ptilou.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skillCode: String,
    val timestampMs: Long,
    val correct: Boolean,
    val tempsReponseMs: Long
)
