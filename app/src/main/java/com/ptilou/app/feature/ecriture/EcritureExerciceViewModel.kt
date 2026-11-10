package com.ptilou.app.feature.ecriture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.audio.Narrateur
import com.ptilou.app.core.audio.SoundEffects
import com.ptilou.app.core.ui.components.HumeurPtilou
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.domain.ThemeApp
import com.ptilou.app.domain.generator.GenerateurExerciceEcriture
import com.ptilou.app.domain.model.CompetenceEcriture
import com.ptilou.app.domain.model.ExerciceEcriture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EcritureExerciceUiState(
    val exercice: ExerciceEcriture,
    val aRepondu: Boolean = false,
    val dernierResultatCorrect: Boolean? = null,
    val messageReessai: String? = null,
    val tentativesEchouees: Int = 0,
    val humeur: HumeurPtilou = HumeurPtilou.REFLECHIT,
    val bonnesReponsesSession: Int = 0,
    val celebrationDeclenchee: Boolean = false
)

// Vraie dictée : le mot n'est jamais affiché à l'écran, seulement lu à voix haute. En cas
// d'erreur, la même dictée est reposée une fois ; ce n'est qu'à la deuxième erreur que la
// réponse est révélée, sans attribution de points.
@HiltViewModel
class EcritureExerciceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProgressionRepository,
    private val gamification: GamificationRepository,
    private val narrateur: Narrateur
) : ViewModel() {

    private val competence = CompetenceEcriture.parCode(checkNotNull(savedStateHandle["skillCode"]))
    private var debutTentativeMs = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(EcritureExerciceUiState(exercice = nouvelExercice()))
    val uiState: StateFlow<EcritureExerciceUiState> = _uiState.asStateFlow()

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        narrateur.lire(_uiState.value.exercice.motAEcrire)
        viewModelScope.launch {
            repository.observerMastery(competence.code).collect { info ->
                if (info.maitrise) gamification.attribuerBadgeSiNouveau(competence.code)
            }
        }
    }

    fun ecouter() {
        narrateur.lire(_uiState.value.exercice.motAEcrire)
    }

    fun valider(reponseSaisie: String) {
        if (_uiState.value.aRepondu || reponseSaisie.isBlank()) return
        val correct = reponseSaisie.trim().equals(_uiState.value.exercice.motAEcrire, ignoreCase = true)
        val tempsReponseMs = System.currentTimeMillis() - debutTentativeMs

        viewModelScope.launch {
            repository.enregistrerTentative(competence.code, correct, tempsReponseMs)
        }

        if (correct) {
            val nouveauCompteurSession = _uiState.value.bonnesReponsesSession + 1
            val paliers5Atteint = nouveauCompteurSession % 5 == 0

            viewModelScope.launch {
                gamification.enregistrerActiviteDuJour()
                gamification.ajouterPieces(1, ThemeApp.ECRITURE)
            }
            if (paliers5Atteint) SoundEffects.jouerRecompense() else SoundEffects.jouerBonneReponse()

            _uiState.update {
                it.copy(
                    aRepondu = true,
                    dernierResultatCorrect = true,
                    messageReessai = null,
                    humeur = HumeurPtilou.CONTENT,
                    bonnesReponsesSession = nouveauCompteurSession,
                    celebrationDeclenchee = paliers5Atteint
                )
            }
        } else {
            SoundEffects.jouerEncouragement()
            val nouvellesTentativesEchouees = _uiState.value.tentativesEchouees + 1
            if (nouvellesTentativesEchouees >= 2) {
                _uiState.update {
                    it.copy(
                        aRepondu = true,
                        dernierResultatCorrect = false,
                        messageReessai = null,
                        tentativesEchouees = nouvellesTentativesEchouees,
                        humeur = HumeurPtilou.NEUTRE
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        tentativesEchouees = nouvellesTentativesEchouees,
                        messageReessai = "Presque ! Écoute encore et réessaie.",
                        humeur = HumeurPtilou.NEUTRE
                    )
                }
                narrateur.lire(_uiState.value.exercice.motAEcrire)
            }
        }
    }

    fun exerciceSuivant() {
        debutTentativeMs = System.currentTimeMillis()
        val exercice = nouvelExercice()
        _uiState.update {
            it.copy(
                exercice = exercice,
                aRepondu = false,
                dernierResultatCorrect = null,
                messageReessai = null,
                tentativesEchouees = 0,
                humeur = HumeurPtilou.REFLECHIT
            )
        }
        narrateur.lire(exercice.motAEcrire)
    }

    fun celebrationTerminee() {
        _uiState.update { it.copy(celebrationDeclenchee = false) }
    }

    private fun nouvelExercice() = GenerateurExerciceEcriture.genererPour(competence)
}
