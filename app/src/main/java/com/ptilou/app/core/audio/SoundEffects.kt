package com.ptilou.app.core.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

// Pas d'assets audio disponibles pour l'instant : tons système ToneGenerator en attendant
// d'éventuels vrais sons/jingles à déposer dans res/raw plus tard. Volume volontairement
// bas pour ne pas surprendre l'enfant.
object SoundEffects {
    private val toneGenerator by lazy { ToneGenerator(AudioManager.STREAM_MUSIC, 55) }
    private val handler = Handler(Looper.getMainLooper())

    // Petite séquence ascendante à deux notes : plus joyeuse qu'un simple bip, sans être forte.
    fun jouerBonneReponse() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
        handler.postDelayed({ toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 130) }, 90)
    }

    // Son bref et doux, jamais un buzzer d'erreur — l'enfant ne doit pas se sentir "puni".
    fun jouerEncouragement() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
    }

    fun jouerRecompense() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 400)
    }
}
