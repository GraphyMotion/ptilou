package com.ptilou.app.data.repository

import com.ptilou.app.data.local.dao.AccessoireDao
import com.ptilou.app.data.local.dao.BadgeDao
import com.ptilou.app.data.local.dao.EconomieDao
import com.ptilou.app.data.local.dao.StreakDao
import com.ptilou.app.data.local.entity.AccessoirePossedeEntity
import com.ptilou.app.data.local.entity.BadgeEntity
import com.ptilou.app.data.local.entity.EconomieEntity
import com.ptilou.app.data.local.entity.StreakEntity
import com.ptilou.app.domain.ReglesJeu
import com.ptilou.app.domain.ThemeApp
import com.ptilou.app.domain.model.Accessoire
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GamificationRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao,
    private val badgeDao: BadgeDao,
    private val economieDao: EconomieDao,
    private val accessoireDao: AccessoireDao
) : GamificationRepository {

    override fun observerStreak(): Flow<StreakInfo> = streakDao.observer().map { entity ->
        val e = entity ?: StreakEntity()
        StreakInfo(e.streakActuel, e.streakMax)
    }

    override fun observerBadges(): Flow<List<BadgeInfo>> = badgeDao.observerTout().map { entities ->
        entities.map { BadgeInfo(it.skillCode, it.dateObtentionMs) }
    }

    override suspend fun enregistrerActiviteDuJour() {
        val aujourdhui = LocalDate.now().toEpochDay()
        val actuel = streakDao.observer().first() ?: StreakEntity()
        val nouveauStreak = when (aujourdhui - actuel.dernierJourActifEpoch) {
            0L -> actuel.streakActuel
            1L -> actuel.streakActuel + 1
            else -> 1
        }
        streakDao.upsert(
            actuel.copy(
                streakActuel = nouveauStreak,
                streakMax = maxOf(actuel.streakMax, nouveauStreak),
                dernierJourActifEpoch = aujourdhui
            )
        )
    }

    override suspend fun attribuerBadgeSiNouveau(skillCode: String) {
        if (badgeDao.compterPour(skillCode) == 0) {
            badgeDao.insert(BadgeEntity(skillCode = skillCode, dateObtentionMs = System.currentTimeMillis()))
        }
    }

    override fun observerPieces(): Flow<Int> = economieDao.observer().map { it?.pieces ?: 0 }

    // Un palier ne se débloque que si les points cumulés depuis le dernier palier
    // proviennent d'au moins THEMES_DISTINCTS_REQUIS thèmes différents — un enfant qui ne
    // fait que le thème qu'il maîtrise déjà n'accumule jamais assez de diversité.
    override fun observerProgressionJeu(): Flow<ProgressionJeu> = economieDao.observer().map { entity ->
        val e = entity ?: return@map ProgressionJeu(0, 0, 0)
        if (e.dernierJourPointsEpoch != LocalDate.now().toEpochDay()) return@map ProgressionJeu(0, 0, 0)
        val compteurs = listOf(e.pointsCalculCycle, e.pointsLectureCycle, e.pointsEcritureCycle, e.pointsDecouverteCycle)
        ProgressionJeu(
            pointsCycleActuel = compteurs.sum(),
            themesDistinctsCycleActuel = compteurs.count { it > 0 },
            creditsDisponibles = (e.creditsGagnesAujourdhui - e.partiesJoueesAujourdhui).coerceAtLeast(0)
        )
    }

    override fun observerCreditsJeuDisponibles(): Flow<Int> = observerProgressionJeu().map { it.creditsDisponibles }

    override suspend fun consommerPartieJeu() {
        val base = entiteDuJour()
        economieDao.upsert(base.copy(partiesJoueesAujourdhui = base.partiesJoueesAujourdhui + 1))
    }

    override fun observerAccessoires(): Flow<List<EtatAccessoire>> = accessoireDao.observerTout().map { possedes ->
        val parId = possedes.associateBy { it.accessoireId }
        Accessoire.entries.map { accessoire ->
            val possede = parId[accessoire.id]
            EtatAccessoire(accessoire, possede = possede != null, equipe = possede?.equipe ?: false)
        }
    }

    override fun observerAccessoireEquipe(): Flow<Accessoire?> = accessoireDao.observerTout().map { possedes ->
        possedes.firstOrNull { it.equipe }?.let { Accessoire.parId(it.accessoireId) }
    }

    override suspend fun ajouterPieces(quantite: Int, theme: ThemeApp) {
        val base = entiteDuJour()

        val pointsCalcul = base.pointsCalculCycle + if (theme == ThemeApp.CALCUL) quantite else 0
        val pointsLecture = base.pointsLectureCycle + if (theme == ThemeApp.LECTURE) quantite else 0
        val pointsEcriture = base.pointsEcritureCycle + if (theme == ThemeApp.ECRITURE) quantite else 0
        val pointsDecouverte = base.pointsDecouverteCycle + if (theme == ThemeApp.DECOUVERTE) quantite else 0

        val totalCycle = pointsCalcul + pointsLecture + pointsEcriture + pointsDecouverte
        val themesDistincts = listOf(pointsCalcul, pointsLecture, pointsEcriture, pointsDecouverte).count { it > 0 }
        val debloquePalier = totalCycle >= ReglesJeu.SEUIL_JEU_QUOTIDIEN && themesDistincts >= ReglesJeu.THEMES_DISTINCTS_REQUIS

        economieDao.upsert(
            base.copy(
                pieces = base.pieces + quantite,
                pointsAujourdhui = base.pointsAujourdhui + quantite,
                creditsGagnesAujourdhui = base.creditsGagnesAujourdhui + if (debloquePalier) ReglesJeu.PARTIES_PAR_PALIER else 0,
                pointsCalculCycle = if (debloquePalier) 0 else pointsCalcul,
                pointsLectureCycle = if (debloquePalier) 0 else pointsLecture,
                pointsEcritureCycle = if (debloquePalier) 0 else pointsEcriture,
                pointsDecouverteCycle = if (debloquePalier) 0 else pointsDecouverte
            )
        )
    }

    // Renvoie l'entité économie en remettant à zéro les compteurs du jour si on a changé
    // de journée depuis la dernière écriture (pièces totales jamais remises à zéro).
    private suspend fun entiteDuJour(): EconomieEntity {
        val actuel = economieDao.observer().first() ?: EconomieEntity()
        val aujourdhui = LocalDate.now().toEpochDay()
        return if (actuel.dernierJourPointsEpoch == aujourdhui) {
            actuel
        } else {
            actuel.copy(
                pointsAujourdhui = 0,
                partiesJoueesAujourdhui = 0,
                creditsGagnesAujourdhui = 0,
                pointsCalculCycle = 0,
                pointsLectureCycle = 0,
                pointsEcritureCycle = 0,
                pointsDecouverteCycle = 0,
                dernierJourPointsEpoch = aujourdhui
            )
        }
    }

    override suspend fun acheter(accessoire: Accessoire): Boolean {
        val dejaPossede = accessoireDao.observerTout().first().any { it.accessoireId == accessoire.id }
        if (dejaPossede) return true

        val economie = economieDao.observer().first() ?: EconomieEntity()
        if (economie.pieces < accessoire.cout) return false

        economieDao.upsert(economie.copy(pieces = economie.pieces - accessoire.cout))
        accessoireDao.upsert(AccessoirePossedeEntity(accessoireId = accessoire.id, equipe = false))
        return true
    }

    override suspend fun equiper(accessoire: Accessoire) {
        accessoireDao.observerTout().first().forEach { possede ->
            if (possede.equipe) accessoireDao.upsert(possede.copy(equipe = false))
        }
        accessoireDao.upsert(AccessoirePossedeEntity(accessoireId = accessoire.id, equipe = true))
    }

    override suspend fun retirerEquipement() {
        accessoireDao.observerTout().first().forEach { possede ->
            if (possede.equipe) accessoireDao.upsert(possede.copy(equipe = false))
        }
    }
}
