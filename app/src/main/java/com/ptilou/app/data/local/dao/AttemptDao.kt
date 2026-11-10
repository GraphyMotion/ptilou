package com.ptilou.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ptilou.app.data.local.entity.AttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Insert
    suspend fun insert(attempt: AttemptEntity)

    @Query("SELECT * FROM attempts WHERE skillCode = :skillCode ORDER BY timestampMs DESC")
    fun observerPour(skillCode: String): Flow<List<AttemptEntity>>
}
