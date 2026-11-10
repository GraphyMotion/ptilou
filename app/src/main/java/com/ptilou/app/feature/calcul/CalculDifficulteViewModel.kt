package com.ptilou.app.feature.calcul

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalculDifficulteViewModel @Inject constructor(
    gamification: GamificationRepository
) : ViewModel() {

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
