package com.ptilou.app.feature.jeux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.audio.SoundEffects
import com.ptilou.app.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// x et y en fraction (0..1) de la zone de jeu : l'écran calcule la position réelle.
data class Bulle(val id: Long, val x: Float, val y: Float, val couleur: Int, val neeA: Long)

data class BullesUiState(
    val bulles: List<Bulle> = emptyList(),
    val score: Int = 0,
    val secondesRestantes: Int = DUREE_PARTIE_S,
    val termine: Boolean = false
)

private const val DUREE_PARTIE_S = 30
private const val DUREE_VIE_BULLE_MS = 2200L
private const val INTERVALLE_APPARITION_MS = 650L

// Jeu de détente : pas de pièces à gagner, comme le Mémory.
@HiltViewModel
class BullesViewModel @Inject constructor(
    private val gamification: GamificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BullesUiState())
    val uiState: StateFlow<BullesUiState> = _uiState.asStateFlow()

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val credits: StateFlow<Int> = gamification.observerCreditsJeuDisponibles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private var partie: Job? = null
    private var prochainId = 0L

    init {
        demarrer()
    }

    private fun demarrer() {
        partie?.cancel()
        _uiState.value = BullesUiState()
        partie = viewModelScope.launch {
            val debut = System.currentTimeMillis()
            while (true) {
                val maintenant = System.currentTimeMillis()
                val restant = DUREE_PARTIE_S - ((maintenant - debut) / 1000).toInt()
                if (restant <= 0) {
                    _uiState.update { it.copy(bulles = emptyList(), secondesRestantes = 0, termine = true) }
                    SoundEffects.jouerRecompense()
                    break
                }
                val nouvelle = Bulle(prochainId++, Random.nextFloat(), Random.nextFloat(), Random.nextInt(5), maintenant)
                _uiState.update { etat ->
                    etat.copy(
                        bulles = etat.bulles.filter { maintenant - it.neeA < DUREE_VIE_BULLE_MS } + nouvelle,
                        secondesRestantes = restant
                    )
                }
                delay(INTERVALLE_APPARITION_MS)
            }
        }
    }

    fun eclater(id: Long) {
        val etat = _uiState.value
        if (etat.termine || etat.bulles.none { it.id == id }) return
        SoundEffects.jouerBonneReponse()
        _uiState.update { it.copy(bulles = it.bulles.filterNot { b -> b.id == id }, score = it.score + 1) }
    }

    fun rejouer() {
        viewModelScope.launch {
            if (credits.value > 0) {
                gamification.consommerPartieJeu()
                demarrer()
            }
        }
    }
}
