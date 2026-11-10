package com.ptilou.app.core.audio

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Lecture à voix haute des consignes/mots — utile pour un enfant de CE1 dont la lecture
// n'est pas encore fluide. Synthèse vocale native Android (gratuite, pas d'assets audio) :
// dépend du pack de voix française installé sur l'appareil (pas garanti par défaut).
@Singleton
class Narrateur @Inject constructor(@ApplicationContext private val context: Context) {
    private var tts: TextToSpeech? = null
    private var pret = false
    // Le moteur se lie de façon asynchrone : un texte demandé avant (ex. 1er mot de dictée)
    // était perdu ou lu avec la voix par défaut. On garde le dernier et on le lit à l'init.
    private var texteEnAttente: String? = null

    private val _voixFrancaiseDisponible = MutableStateFlow(true)
    val voixFrancaiseDisponible: StateFlow<Boolean> = _voixFrancaiseDisponible.asStateFlow()

    init {
        tts = TextToSpeech(context) { statut ->
            if (statut == TextToSpeech.SUCCESS) {
                val resultat = tts?.setLanguage(Locale.FRANCE)
                _voixFrancaiseDisponible.value = resultat != TextToSpeech.LANG_MISSING_DATA &&
                    resultat != TextToSpeech.LANG_NOT_SUPPORTED
                pret = true
                texteEnAttente?.let { lire(it) }
                texteEnAttente = null
            } else {
                _voixFrancaiseDisponible.value = false
            }
        }
    }

    fun lire(texte: String) {
        if (texte.isBlank()) return
        if (!pret) {
            texteEnAttente = texte
            return
        }
        tts?.speak(texte, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun ouvrirInstallationVoixFrancaise() {
        val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }
}
