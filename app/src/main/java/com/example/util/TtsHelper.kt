package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsHelper(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var pendingLang: String = "gu"

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TtsHelper", "Failed to instantiate TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })

            pendingText?.let { text ->
                speak(text, pendingLang, 0.85f)
                pendingText = null
            }
        } else {
            Log.e("TtsHelper", "TTS Initialization failed with status: $status")
        }
    }

    fun speak(text: String, langCode: String = "gu", speed: Float = 0.85f) {
        if (text.isBlank()) return

        if (!isInitialized || tts == null) {
            pendingText = text
            pendingLang = langCode
            return
        }

        try {
            val locale = when (langCode.lowercase()) {
                "hi" -> Locale("hi", "IN")
                "gu" -> Locale("gu", "IN")
                else -> Locale("en", "US")
            }

            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English or default
                tts?.language = Locale.getDefault()
            }

            tts?.setSpeechRate(speed.coerceIn(0.6f, 1.2f))
            tts?.setPitch(1.0f)

            val utteranceId = "MannSaathi_${System.currentTimeMillis()}"
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("TtsHelper", "Error speaking text: ${e.message}")
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("TtsHelper", "Error stopping TTS: ${e.message}")
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("TtsHelper", "Error shutting down TTS: ${e.message}")
        }
    }
}
