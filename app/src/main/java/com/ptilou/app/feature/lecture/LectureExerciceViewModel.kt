package com.ptilou.app.feature.lecture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.audio.Narrateur
import com.ptilou.app.core.audio.SoundEffects
import com.ptilou.app.core.ui.components.HumeurPtilou
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.domain.ThemeApp
import com.ptilou.app.domain.generator.GenerateurExerciceLecture
import com.ptilou.app.domain.model.CompetenceLecture
import com.ptilou.app.domain.model.ExerciceLecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LectureExerciceUiState(
    val exercice: ExerciceLecture,
    val selectionIndex: Int? = null,
    val aRepondu: Boolean = false,
    val dernierResultatCorrect: Boolean? = null,
    val messageReessai: String? = null,
    val tentativesEchouees: Int = 0,
    val humeur: HumeurPtilou = HumeurPtilou.REFLECHIT,
    val bonnesReponsesSession: Int = 0,
    val celebrationDeclenchee: Boolean = false
)

// En cas d'erreur, la même question est reposée une fois ; ce n'est qu'à la deuxième
// erreur que la réponse est révélée, sans attribution de points.
@HiltViewModel
class LectureExerciceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProgressionRepository,
    private val gamification: GamificationRepository,
    private val narrateur: Narrateur
) : ViewModel() {

    private val competence = CompetenceLecture.parCode(checkNotNull(savedStateHandle["skillCode"]))
    private var debutTentativeMs = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(LectureExerciceUiState(exercice = nouvelExercice()))
    val uiState: StateFlow<LectureExerciceUiState> = _uiState.asStateFlow()

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Compter les syllabes est un exercice oral par nature (segmentation phonologique) :
        // l'enfant a besoin d'entendre le mot, pas seulement de le lire.
        if (competence == CompetenceLecture.RECONNAISSANCE_SYLLABES) {
            narrateur.lire(_uiState.value.exercice.texte)
        }
        viewModelScope.launch {
            repository.observerMastery(competence.code).collect { info ->
                if (info.maitrise) gamification.attribuerBadgeSiNouveau(competence.code)
            }
        }
    }

    fun ecouter() {
        val exercice = _uiState.value.exercice
        narrateur.lire(exercice.texte.ifBlank { exercice.question })
    }

    fun choisir(index: Int) {
        if (_uiState.value.aRepondu) return
        val correct = index == _uiState.value.exercice.bonneReponseIndex
        val tempsReponseMs = System.currentTimeMillis() - debutTentativeMs

        viewModelScope.launch {
            repository.enregistrerTentative(competence.code, correct, tempsReponseMs)
        }

        if (correct) {
            val nouveauCompteurSession = _uiState.value.bonnesReponsesSession + 1
            val paliers5Atteint = nouveauCompteurSession % 5 == 0

            viewModelScope.launch {
                gamification.enregistrerActiviteDuJour()
                gamification.ajouterPieces(1, ThemeApp.LECTURE)
            }
            if (paliers5Atteint) SoundEffects.jouerRecompense() else SoundEffects.jouerBonneReponse()

            _uiState.update {
                it.copy(
                    selectionIndex = index,
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
                        selectionIndex = index,
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
                        selectionIndex = null,
                        tentativesEchouees = nouvellesTentativesEchouees,
                        messageReessai = "Presque ! Essaie encore.",
                        humeur = HumeurPtilou.NEUTRE
                    )
                }
            }
        }
    }

    fun exerciceSuivant() {
        debutTentativeMs = System.currentTimeMillis()
        val exercice = nouvelExercice()
        _uiState.update {
            it.copy(
                exercice = exercice,
                selectionIndex = null,
                aRepondu = false,
                dernierResultatCorrect = null,
                messageReessai = null,
                tentativesEchouees = 0,
                humeur = HumeurPtilou.REFLECHIT
            )
        }
        if (competence == CompetenceLecture.RECONNAISSANCE_SYLLABES) {
            narrateur.lire(exercice.texte)
        }
    }

    fun celebrationTerminee() {
        _uiState.update { it.copy(celebrationDeclenchee = false) }
    }

    private fun nouvelExercice() = GenerateurExerciceLecture.genererPour(competence)
}
