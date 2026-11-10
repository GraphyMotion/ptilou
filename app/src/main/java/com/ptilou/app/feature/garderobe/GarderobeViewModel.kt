package com.ptilou.app.feature.garderobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.data.repository.EtatAccessoire
import com.ptilou.app.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GarderobeUiState(
    val pieces: Int = 0,
    val accessoires: List<EtatAccessoire> = emptyList()
)

@HiltViewModel
class GarderobeViewModel @Inject constructor(
    private val gamification: GamificationRepository
) : ViewModel() {

    val uiState: StateFlow<GarderobeUiState> = combine(
        gamification.observerPieces(),
        gamification.observerAccessoires()
    ) { pieces, accessoires -> GarderobeUiState(pieces, accessoires) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GarderobeUiState())

    fun onClicAccessoire(etat: EtatAccessoire) {
        viewModelScope.launch {
            when {
                etat.equipe -> gamification.retirerEquipement()
                etat.possede -> gamification.equiper(etat.accessoire)
                else -> gamification.acheter(etat.accessoire)
            }
        }
    }
}
