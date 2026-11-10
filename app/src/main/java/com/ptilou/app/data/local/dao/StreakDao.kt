package com.ptilou.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ptilou.app.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak WHERE id = 1")
    fun observer(): Flow<StreakEntity?>

    @Upsert
    suspend fun upsert(streak: StreakEntity)
}
