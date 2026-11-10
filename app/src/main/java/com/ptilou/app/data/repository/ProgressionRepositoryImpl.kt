package com.ptilou.app.data.repository

import com.ptilou.app.data.local.dao.AttemptDao
import com.ptilou.app.data.local.dao.MasteryDao
import com.ptilou.app.data.local.entity.AttemptEntity
import com.ptilou.app.data.local.entity.MasteryEntity
import com.ptilou.app.domain.MasteryCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressionRepositoryImpl @Inject constructor(
    private val masteryDao: MasteryDao,
    private val attemptDao: AttemptDao
) : ProgressionRepository {

    override fun observerMastery(skillCode: String): Flow<MasteryInfo> =
        masteryDao.observer(skillCode).map { entity -> versInfo(entity ?: MasteryEntity(skillCode = skillCode)) }

    override fun observerToutesMasteries(): Flow<List<MasteryInfo>> =
        masteryDao.observerTout().map { entities -> entities.map(::versInfo) }

    override suspend fun enregistrerTentative(skillCode: String, correct: Boolean, tempsReponseMs: Long) {
        val actuel = masteryDao.observer(skillCode).first() ?: MasteryEntity(skillCode = skillCode)
        val nouveauScore = MasteryCalculator.miseAJourScore(actuel.score, correct)
        val nouvellesTentatives = actuel.attempts + 1
        val nouveauPalier = MasteryCalculator.calculerPalier(nouveauScore, nouvellesTentatives)

        masteryDao.upsert(
            actuel.copy(
                score = nouveauScore,
                attempts = nouvellesTentatives,
                correct = actuel.correct + if (correct) 1 else 0,
                lastSeen = System.currentTimeMillis(),
                palier = nouveauPalier
            )
        )
        attemptDao.insert(
            AttemptEntity(
                skillCode = skillCode,
                timestampMs = System.currentTimeMillis(),
                correct = correct,
                tempsReponseMs = tempsReponseMs
            )
        )
    }

    private fun versInfo(entity: MasteryEntity) = MasteryInfo(
        skillCode = entity.skillCode,
        score = entity.score,
        attempts = entity.attempts,
        correct = entity.correct,
        palier = entity.palier,
        maitrise = entity.score >= MasteryCalculator.SEUIL_MAITRISE
    )
}
