package com.ptilou.app.feature.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.data.local.PtilouPreferences
import com.ptilou.app.domain.ReglesJeu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempsViewModel @Inject constructor(
    private val preferences: PtilouPreferences
) : ViewModel() {

    val minutesRestantes: StateFlow<Int> = preferences.minutesRestantes
        .stateIn(viewModelScope, SharingStarted.Eagerly, ReglesJeu.MINUTES_PAR_JOUR)

    fun ajouterMinute() {
        viewModelScope.launch { preferences.ajouterMinuteUtilisee() }
    }

    fun accorderBonus() {
        viewModelScope.launch { preferences.accorderBonus() }
    }
}
