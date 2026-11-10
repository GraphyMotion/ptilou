package com.ptilou.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ptilou.app.data.local.entity.AccessoirePossedeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessoireDao {
    @Query("SELECT * FROM accessoires_possedes")
    fun observerTout(): Flow<List<AccessoirePossedeEntity>>

    @Upsert
    suspend fun upsert(accessoire: AccessoirePossedeEntity)
}
