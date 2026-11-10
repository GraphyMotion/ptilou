package com.ptilou.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ptilou.app.data.local.entity.EconomieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EconomieDao {
    @Query("SELECT * FROM economie WHERE id = 1")
    fun observer(): Flow<EconomieEntity?>

    @Upsert
    suspend fun upsert(economie: EconomieEntity)
}
