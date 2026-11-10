package com.ptilou.app.feature.jeux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Choisir un jeu consomme une partie ; "Rejouer" dans chaque jeu en consomme une autre.
@HiltViewModel
class JeuxViewModel @Inject constructor(
    private val gamification: GamificationRepository
) : ViewModel() {

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val credits: StateFlow<Int> = gamification.observerCreditsJeuDisponibles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun consommerPartie() {
        viewModelScope.launch { gamification.consommerPartieJeu() }
    }
}
