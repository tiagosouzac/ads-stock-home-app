package com.example.stockhome.ui.components

import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View

/**
 * Feedback de toque reutilizável (som curto + vibração leve), sem precisar de
 * arquivos de áudio. O tom é gerado em tempo de execução pelo [ToneGenerator],
 * então funciona em qualquer aparelho e respeita o volume do sistema.
 */
object ClickFeedback {
    @Volatile private var tone: ToneGenerator? = null

    private fun generator(): ToneGenerator? {
        if (tone == null) {
            tone = try {
                // Volume moderado (0–100) no canal de sons do sistema.
                ToneGenerator(AudioManager.STREAM_SYSTEM, 65)
            } catch (e: RuntimeException) {
                null
            }
        }
        return tone
    }

    /** Toca um "tick" curto e dá um leve retorno tátil. */
    fun play(view: View) {
        try {
            // Som do sistema (respeita as preferências de toque do usuário)…
            view.playSoundEffect(SoundEffectConstants.CLICK)
            // …e um tom curto garantido, caso os sons de toque estejam desativados.
            generator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (_: Exception) {
            // Áudio é um extra: nunca deixa um erro de som quebrar a interação.
        }
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {
        }
    }
}
