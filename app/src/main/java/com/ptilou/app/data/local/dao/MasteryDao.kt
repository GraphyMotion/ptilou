package com.ptilou.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ptilou.app.data.local.entity.MasteryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MasteryDao {
    @Query("SELECT * FROM mastery WHERE skillCode = :skillCode")
    fun observer(skillCode: String): Flow<MasteryEntity?>

    @Query("SELECT * FROM mastery")
    fun observerTout(): Flow<List<MasteryEntity>>

    @Upsert
    suspend fun upsert(mastery: MasteryEntity)
}
