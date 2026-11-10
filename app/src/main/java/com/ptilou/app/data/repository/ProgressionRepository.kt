package com.ptilou.app.data.repository

import kotlinx.coroutines.flow.Flow

data class MasteryInfo(
    val skillCode: String,
    val score: Float,
    val attempts: Int,
    val correct: Int,
    val palier: Int,
    val maitrise: Boolean
)

interface ProgressionRepository {
    fun observerMastery(skillCode: String): Flow<MasteryInfo>
    fun observerToutesMasteries(): Flow<List<MasteryInfo>>
    suspend fun enregistrerTentative(skillCode: String, correct: Boolean, tempsReponseMs: Long)
}
