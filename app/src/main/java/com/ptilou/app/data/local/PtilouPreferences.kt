package com.ptilou.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ptilou.app.domain.ReglesJeu
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "ptilou_prefs")
private val CLE_PRENOM = stringPreferencesKey("prenom_enfant")
private val CLE_JOUR_USAGE = longPreferencesKey("jour_usage")
private val CLE_MINUTES_USAGE = intPreferencesKey("minutes_usage")
private val CLE_MINUTES_BONUS = intPreferencesKey("minutes_bonus")

// Préférences simples de l'app (prénom, temps d'écran du jour) — DataStore plutôt que
// Room, plus adapté à ces quelques valeurs clé/valeur non structurées.
@Singleton
class PtilouPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    val prenom: Flow<String?> = context.dataStore.data.map { it[CLE_PRENOM] }

    suspend fun enregistrerPrenom(prenom: String) {
        context.dataStore.edit { it[CLE_PRENOM] = prenom }
    }

    val minutesRestantes: Flow<Int> = context.dataStore.data.map { p ->
        if (p[CLE_JOUR_USAGE] != aujourdhui()) {
            ReglesJeu.MINUTES_PAR_JOUR
        } else {
            ReglesJeu.MINUTES_PAR_JOUR + (p[CLE_MINUTES_BONUS] ?: 0) - (p[CLE_MINUTES_USAGE] ?: 0)
        }
    }

    // Ne compte plus une fois la limite atteinte : sinon les minutes passées devant l'écran
    // « Ptilou est fatigué » mangeraient le temps accordé ensuite par le parent.
    suspend fun ajouterMinuteUtilisee() = modifierJour { p ->
        val utilisees = p[CLE_MINUTES_USAGE] ?: 0
        if (utilisees < ReglesJeu.MINUTES_PAR_JOUR + (p[CLE_MINUTES_BONUS] ?: 0)) {
            p[CLE_MINUTES_USAGE] = utilisees + 1
        }
    }

    suspend fun accorderBonus() = modifierJour { p ->
        p[CLE_MINUTES_BONUS] = (p[CLE_MINUTES_BONUS] ?: 0) + ReglesJeu.MINUTES_BONUS
    }

    private suspend fun modifierJour(action: (MutablePreferences) -> Unit) {
        context.dataStore.edit { p ->
            if (p[CLE_JOUR_USAGE] != aujourdhui()) {
                p[CLE_JOUR_USAGE] = aujourdhui()
                p[CLE_MINUTES_USAGE] = 0
                p[CLE_MINUTES_BONUS] = 0
            }
            action(p)
        }
    }

    private fun aujourdhui() = LocalDate.now().toEpochDay()
}
