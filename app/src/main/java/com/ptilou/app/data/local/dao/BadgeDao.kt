package com.ptilou.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ptilou.app.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert
    suspend fun insert(badge: BadgeEntity)

    @Query("SELECT * FROM badges ORDER BY dateObtentionMs DESC")
    fun observerTout(): Flow<List<BadgeEntity>>

    @Query("SELECT COUNT(*) FROM badges WHERE skillCode = :skillCode")
    suspend fun compterPour(skillCode: String): Int
}
