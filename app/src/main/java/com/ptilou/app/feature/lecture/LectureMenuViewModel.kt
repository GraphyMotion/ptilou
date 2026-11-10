package com.ptilou.app.feature.lecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.ui.model.CompetenceUi
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.domain.model.CompetenceLecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LectureMenuViewModel @Inject constructor(
    repository: ProgressionRepository,
    gamification: GamificationRepository
) : ViewModel() {

    val competences: StateFlow<List<CompetenceUi>> = repository.observerToutesMasteries()
        .map { masteries ->
            val parCode = masteries.associateBy { it.skillCode }
            CompetenceLecture.entries.map { competence ->
                val info = parCode[competence.code]
                CompetenceUi(
                    code = competence.code,
                    libelle = competence.libelle,
                    score = info?.score ?: 0f,
                    maitrise = info?.maitrise ?: false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
