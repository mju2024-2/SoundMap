package com.example.speechrecognizer.controller

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TextToSpeechManager(
    context: Context,
    private val language: Locale = Locale.KOREAN
) : TextToSpeech.OnInitListener {

    private val textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var initialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(language)
            initialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!initialized) {
                Log.e("TextToSpeechManager", "Language not supported.")
            } else {
                Log.d("TextToSpeechManager", "Initialization successful.")
            }
        } else {
            Log.e("TextToSpeechManager", "Initialization failed.")
        }
    }

    fun speak(text: String) {
        if (initialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TextToSpeechManager", "TTS not initialized.")
        }
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
