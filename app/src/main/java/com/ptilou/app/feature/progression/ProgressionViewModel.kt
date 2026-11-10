package com.ptilou.app.feature.progression

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.ui.model.CompetenceUi
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.data.repository.StreakInfo
import com.ptilou.app.domain.model.TOUTES_LES_COMPETENCES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProgressionUiState(
    val streak: StreakInfo = StreakInfo(0, 0),
    val competences: List<CompetenceUi> = emptyList(),
    val nombreBadges: Int = 0,
    val pieces: Int = 0
)

@HiltViewModel
class ProgressionViewModel @Inject constructor(
    progressionRepository: ProgressionRepository,
    gamificationRepository: GamificationRepository
) : ViewModel() {

    val uiState: StateFlow<ProgressionUiState> = combine(
        gamificationRepository.observerStreak(),
        progressionRepository.observerToutesMasteries(),
        gamificationRepository.observerBadges(),
        gamificationRepository.observerPieces()
    ) { streak, masteries, badges, pieces ->
        val parCode = masteries.associateBy { it.skillCode }
        val competences = TOUTES_LES_COMPETENCES.map { (code, libelle) ->
            val info = parCode[code]
            CompetenceUi(
                code = code,
                libelle = libelle,
                score = info?.score ?: 0f,
                maitrise = info?.maitrise ?: false
            )
        }
        ProgressionUiState(streak = streak, competences = competences, nombreBadges = badges.size, pieces = pieces)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressionUiState())
}
