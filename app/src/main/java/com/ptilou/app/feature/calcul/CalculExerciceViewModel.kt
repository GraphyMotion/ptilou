package com.ptilou.app.feature.calcul

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.audio.Narrateur
import com.ptilou.app.core.audio.SoundEffects
import com.ptilou.app.core.ui.components.HumeurPtilou
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.domain.ThemeApp
import com.ptilou.app.domain.generator.GenerateurExerciceCalcul
import com.ptilou.app.domain.model.CompetenceCalcul
import com.ptilou.app.domain.model.ExerciceCalcul
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculExerciceUiState(
    val exercice: ExerciceCalcul,
    val aRepondu: Boolean = false,
    val dernierResultatCorrect: Boolean? = null,
    val messageReessai: String? = null,
    val tentativesEchouees: Int = 0,
    val humeur: HumeurPtilou = HumeurPtilou.REFLECHIT,
    val bonnesReponsesSession: Int = 0,
    val celebrationDeclenchee: Boolean = false
)

// Le niveau (Facile/Moyen/Difficile) est choisi explicitement par l'enfant, pas dérivé
// automatiquement du score de maîtrise — plus lisible pour un enfant de CE1. Le score EMA
// continue d'être mis à jour en arrière-plan pour le badge "maîtrisé" affiché dans le menu.
// En cas d'erreur, la même question est reposée une fois ; ce n'est qu'à la deuxième
// erreur que la réponse est révélée, sans attribution de points.
@HiltViewModel
class CalculExerciceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProgressionRepository,
    private val gamification: GamificationRepository,
    private val narrateur: Narrateur
) : ViewModel() {

    private val competence = CompetenceCalcul.parCode(checkNotNull(savedStateHandle["skillCode"]))
    private val niveau: Int = checkNotNull(savedStateHandle["niveau"])
    private var debutTentativeMs = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(CalculExerciceUiState(exercice = nouvelExercice()))
    val uiState: StateFlow<CalculExerciceUiState> = _uiState.asStateFlow()

    val pieces: StateFlow<Int> = gamification.observerPieces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.observerMastery(competence.code).collect { info ->
                if (info.maitrise) gamification.attribuerBadgeSiNouveau(competence.code)
            }
        }
    }

    // La synthèse vocale lit mal les symboles ("2 x 5" → "deux ixe cinq") : on les traduit.
    fun ecouter() {
        narrateur.lire(
            _uiState.value.exercice.enonce
                .replace(" x ", " fois ")
                .replace(" ÷ ", " divisé par ")
                .replace(" - ", " moins ")
                .replace(" + ", " plus ")
                .replace("= ?", "égale combien ?")
        )
    }

    fun valider(reponseSaisie: Int) {
        if (_uiState.value.aRepondu) return
        val correct = reponseSaisie == _uiState.value.exercice.reponseAttendue
        val tempsReponseMs = System.currentTimeMillis() - debutTentativeMs

        viewModelScope.launch {
            repository.enregistrerTentative(competence.code, correct, tempsReponseMs)
        }

        if (correct) {
            // Plus le niveau choisi est difficile, plus une bonne réponse rapporte de
            // pièces (Facile = 1, Moyen = 2, Difficile = 3).
            val pointsGagnes = niveau + 1
            val nouveauCompteurSession = _uiState.value.bonnesReponsesSession + 1
            val paliers5Atteint = nouveauCompteurSession % 5 == 0

            viewModelScope.launch {
                gamification.enregistrerActiviteDuJour()
                gamification.ajouterPieces(pointsGagnes, ThemeApp.CALCUL)
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
                        messageReessai = "Presque ! Essaie encore.",
                        humeur = HumeurPtilou.NEUTRE
                    )
                }
            }
        }
    }

    fun exerciceSuivant() {
        debutTentativeMs = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                exercice = nouvelExercice(),
                aRepondu = false,
                dernierResultatCorrect = null,
                messageReessai = null,
                tentativesEchouees = 0,
                humeur = HumeurPtilou.REFLECHIT
            )
        }
    }

    fun celebrationTerminee() {
        _uiState.update { it.copy(celebrationDeclenchee = false) }
    }

    private fun nouvelExercice() = GenerateurExerciceCalcul.genererPour(competence, niveau)
}
