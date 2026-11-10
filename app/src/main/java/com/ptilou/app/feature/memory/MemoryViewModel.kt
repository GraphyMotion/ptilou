package com.ptilou.app.feature.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.audio.SoundEffects
import com.ptilou.app.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoryUiState(
    val cartes: List<CarteMemory> = emptyList(),
    val pairesTrouvees: Int = 0,
    val jeuTermine: Boolean = false,
    val enAttenteVerification: Boolean = false
)

// Pur jeu de détente débloqué par les points du jour : pas de pièces à gagner ici,
// le jeu EST la récompense.
@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val gamification: GamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryUiState(cartes = nouvellesCartes()))
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Une partie a déjà été consommée à l'entrée dans l'écran (depuis l'accueil) ; "Rejouer"
    // n'est proposé que s'il reste des crédits, sinon il faut regagner des points.
    val creditsJeu: StateFlow<Int> = gamification.observerCreditsJeuDisponibles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun retournerCarte(id: Int) {
        val etat = _uiState.value
        if (etat.enAttenteVerification || etat.jeuTermine) return
        val carte = etat.cartes.find { it.id == id } ?: return
        if (carte.retournee || carte.trouvee) return

        val cartesApresRetournement = etat.cartes.map { if (it.id == id) it.copy(retournee = true) else it }
        val retournees = cartesApresRetournement.filter { it.retournee && !it.trouvee }

        if (retournees.size < 2) {
            _uiState.update { it.copy(cartes = cartesApresRetournement) }
            return
        }

        val (premiere, seconde) = retournees
        if (premiere.symbole == seconde.symbole) {
            val cartesTrouvees = cartesApresRetournement.map {
                if (it.id == premiere.id || it.id == seconde.id) it.copy(trouvee = true) else it
            }
            val pairesTrouvees = etat.pairesTrouvees + 1
            SoundEffects.jouerBonneReponse()
            _uiState.update {
                it.copy(
                    cartes = cartesTrouvees,
                    pairesTrouvees = pairesTrouvees,
                    jeuTermine = pairesTrouvees == cartesTrouvees.size / 2
                )
            }
            if (pairesTrouvees == cartesTrouvees.size / 2) {
                SoundEffects.jouerRecompense()
            }
        } else {
            _uiState.update { it.copy(cartes = cartesApresRetournement, enAttenteVerification = true) }
            viewModelScope.launch {
                delay(900)
                _uiState.update { actuel ->
                    actuel.copy(
                        cartes = actuel.cartes.map { c -> if (!c.trouvee) c.copy(retournee = false) else c },
                        enAttenteVerification = false
                    )
                }
            }
        }
    }

    fun rejouer() {
        viewModelScope.launch {
            if (creditsJeu.value > 0) {
                gamification.consommerPartieJeu()
                _uiState.value = MemoryUiState(cartes = nouvellesCartes())
            }
        }
    }

    private fun nouvellesCartes(): List<CarteMemory> {
        val symboles = listOf("🐶", "🐱", "🐰", "🦁", "🍎", "🍓", "🚀", "⚽")
        return (symboles + symboles).shuffled().mapIndexed { index, symbole -> CarteMemory(index, symbole) }
    }
}
