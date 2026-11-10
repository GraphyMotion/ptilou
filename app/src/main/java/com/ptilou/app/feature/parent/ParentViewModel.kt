package com.ptilou.app.feature.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptilou.app.core.miseajour.MiseAJour
import com.ptilou.app.core.miseajour.Nouveaute
import com.ptilou.app.data.local.PtilouPreferences
import com.ptilou.app.data.repository.GamificationRepository
import com.ptilou.app.data.repository.ProgressionRepository
import com.ptilou.app.domain.ReglesJeu
import com.ptilou.app.domain.model.TOUTES_LES_COMPETENCES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class LigneParent(val libelle: String, val tentatives: Int, val tauxReussite: Int)

data class ParentUiState(
    val lignes: List<LigneParent> = emptyList(),
    val minutesRestantes: Int = ReglesJeu.MINUTES_PAR_JOUR,
    val pieces: Int = 0
)

// Réutilise les compteurs déjà tenus par compétence (essais / réussites) : aucune nouvelle
// donnée collectée, tout reste sur la tablette.
@HiltViewModel
class ParentViewModel @Inject constructor(
    progression: ProgressionRepository,
    gamification: GamificationRepository,
    private val preferences: PtilouPreferences,
    private val miseAJour: MiseAJour
) : ViewModel() {

    val uiState: StateFlow<ParentUiState> = combine(
        progression.observerToutesMasteries(),
        preferences.minutesRestantes,
        gamification.observerPieces()
    ) { masteries, minutes, pieces ->
        val parCode = masteries.associateBy { it.skillCode }
        val lignes = TOUTES_LES_COMPETENCES.map { (code, libelle) ->
            val m = parCode[code]
            val tentatives = m?.attempts ?: 0
            LigneParent(libelle, tentatives, if (tentatives == 0) 0 else m!!.correct * 100 / tentatives)
        }
            // Les plus fragiles d'abord, les jamais pratiquées (évitées ?) à la fin.
            .sortedWith(compareBy({ it.tentatives == 0 }, { it.tauxReussite }))
        ParentUiState(lignes, minutes, pieces)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParentUiState())

    fun accorderBonus() {
        viewModelScope.launch { preferences.accorderBonus() }
    }

    // --- Mise à jour depuis GitHub (vérifiée à chaque ouverture de l'espace parent) ---

    val versionActuelle = miseAJour.versionActuelle

    private val _maj = MutableStateFlow<EtatMaj>(EtatMaj.Verification)
    val maj: StateFlow<EtatMaj> = _maj.asStateFlow()

    private var apkTelecharge: File? = null

    init {
        verifierMaj()
    }

    fun verifierMaj() {
        _maj.value = EtatMaj.Verification
        viewModelScope.launch {
            _maj.value = runCatching { miseAJour.verifier() }.fold(
                onSuccess = { it?.let(EtatMaj::Disponible) ?: EtatMaj.AJour },
                onFailure = { EtatMaj.Erreur("Pas de connexion à Internet ?") }
            )
        }
    }

    fun installerMaj() {
        apkTelecharge?.let { lancerInstallation(it); return }
        val nouveaute = (_maj.value as? EtatMaj.Disponible)?.nouveaute ?: return
        viewModelScope.launch {
            _maj.value = EtatMaj.Telechargement(0f)
            runCatching { miseAJour.telecharger(nouveaute) { _maj.value = EtatMaj.Telechargement(it) } }
                .onSuccess { apkTelecharge = it; lancerInstallation(it) }
                .onFailure { _maj.value = EtatMaj.Erreur("Téléchargement interrompu, réessaie.") }
        }
    }

    private fun lancerInstallation(fichier: File) {
        _maj.value = if (miseAJour.installer(fichier)) EtatMaj.Pret else EtatMaj.AutorisationRequise
    }
}

sealed interface EtatMaj {
    data object Verification : EtatMaj
    data object AJour : EtatMaj
    data class Disponible(val nouveaute: Nouveaute) : EtatMaj
    data class Telechargement(val progres: Float) : EtatMaj
    data object AutorisationRequise : EtatMaj
    data object Pret : EtatMaj
    data class Erreur(val message: String) : EtatMaj
}
