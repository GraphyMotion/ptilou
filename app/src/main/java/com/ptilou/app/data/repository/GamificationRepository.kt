package com.ptilou.app.data.repository

import com.ptilou.app.domain.ThemeApp
import com.ptilou.app.domain.model.Accessoire
import kotlinx.coroutines.flow.Flow

data class StreakInfo(val streakActuel: Int, val streakMax: Int)
data class BadgeInfo(val skillCode: String, val dateObtentionMs: Long)
data class EtatAccessoire(val accessoire: Accessoire, val possede: Boolean, val equipe: Boolean)
data class ProgressionJeu(val pointsCycleActuel: Int, val themesDistinctsCycleActuel: Int, val creditsDisponibles: Int)

interface GamificationRepository {
    fun observerStreak(): Flow<StreakInfo>
    fun observerBadges(): Flow<List<BadgeInfo>>
    suspend fun enregistrerActiviteDuJour()
    suspend fun attribuerBadgeSiNouveau(skillCode: String)

    fun observerPieces(): Flow<Int>
    fun observerProgressionJeu(): Flow<ProgressionJeu>
    fun observerCreditsJeuDisponibles(): Flow<Int>
    suspend fun consommerPartieJeu()
    fun observerAccessoires(): Flow<List<EtatAccessoire>>
    fun observerAccessoireEquipe(): Flow<Accessoire?>
    suspend fun ajouterPieces(quantite: Int, theme: ThemeApp)
    suspend fun acheter(accessoire: Accessoire): Boolean
    suspend fun equiper(accessoire: Accessoire)
    suspend fun retirerEquipement()
}
