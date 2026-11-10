package com.ptilou.app.feature.accueil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.audio.Narrateur
import com.ptilou.app.data.local.PtilouPreferences
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionJeu
import com.ptilou.app.domain.model.Accessoire
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccueilViewModel @Inject constructor(
    private val gamification: GamificationRepository,
    private val narrateur: Narrateur,
    private val preferences: PtilouPreferences
) : ViewModel() {

    val accessoireEquipe: StateFlow<Accessoire?> = gamification.observerAccessoireEquipe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val voixFrancaiseDisponible: StateFlow<Boolean> = narrateur.voixFrancaiseDisponible
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val progressionJeu: StateFlow<ProgressionJeu> = gamification.observerProgressionJeu()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressionJeu(0, 0, 0))

    val prenom: StateFlow<String?> = preferences.prenom
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun installerVoixFrancaise() {
        narrateur.ouvrirInstallationVoixFrancaise()
    }

    fun enregistrerPrenom(prenom: String) {
        viewModelScope.launch { preferences.enregistrerPrenom(prenom) }
    }
}
