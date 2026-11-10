package com.ptilou.app.feature.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.data.local.PtilouPreferences
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.domain.ReglesJeu
import com.ptilou.app.domain.model.TOUTES_LES_COMPETENCES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LigneParent(val libelle: String, val tentatives: Int, val tauxReussite: Int)

data class ParentUiState(
    val lignes: List<LigneParent> = emptyList(),
    val minutesRestantes: Int = ReglesJeu.MINUTES_PAR_JOUR,
    val pieces: Int = 0
)

// Réutilise les compteurs déjà tenus par compétence (essais / réussites) : aucune nouvelle
// donnée collectée, tout reste sur la tablette.
@HiltViewModel
class ParentViewModel @Inject constructor(
    progression: ProgressionRepository,
    gamification: GamificationRepository,
    private val preferences: PtilouPreferences
) : ViewModel() {

    val uiState: StateFlow<ParentUiState> = combine(
        progression.observerToutesMasteries(),
        preferences.minutesRestantes,
        gamification.observerPieces()
    ) { masteries, minutes, pieces ->
        val parCode = masteries.associateBy { it.skillCode }
        val lignes = TOUTES_LES_COMPETENCES.map { (code, libelle) ->
            val m = parCode[code]
            val tentatives = m?.attempts ?: 0
            LigneParent(libelle, tentatives, if (tentatives == 0) 0 else m!!.correct * 100 / tentatives)
        }
            // Les plus fragiles d'abord, les jamais pratiquées (évitées ?) à la fin.
            .sortedWith(compareBy({ it.tentatives == 0 }, { it.tauxReussite }))
        ParentUiState(lignes, minutes, pieces)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParentUiState())

    fun accorderBonus() {
        viewModelScope.launch { preferences.accorderBonus() }
    }
}
