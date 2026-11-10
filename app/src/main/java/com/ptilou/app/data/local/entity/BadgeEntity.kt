package com.ptilou.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skillCode: String,
    val dateObtentionMs: Long
)
