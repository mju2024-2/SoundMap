package com.example.soundmap.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TextToSpeechService(
    context: Context,
    private val language: Locale = Locale.KOREAN
) : TextToSpeech.OnInitListener {

    private val textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var initialized = false
    private val pendingTextQueue = mutableListOf<String>() // 음성 요청 대기 리스트

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(language)
            initialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!initialized) {
                Log.e("TextToSpeechManager", "Language not supported.")
            } else {
                Log.d("TextToSpeechManager", "Initialization successful.")
                // 초기화가 완료되면 대기 중인 텍스트들을 모두 처리
                processPendingTextQueue()
            }
        } else {
            Log.e("TextToSpeechManager", "Initialization failed.")
        }
    }

    // 음성 요청을 큐에 추가
    fun speak(text: String) {
        if (initialized) {
            Log.d("TextToSpeechManager", "Speaking text: $text")
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.d("TextToSpeechManager", "TTS not initialized, queuing text.")
            // 초기화가 안되었으면 대기 큐에 추가
            pendingTextQueue.add(text)
        }
    }

    // 초기화 완료 후 대기 중인 텍스트들 처리
    private fun processPendingTextQueue() {
        while (pendingTextQueue.isNotEmpty()) {
            val text = pendingTextQueue.removeAt(0)
            speak(text)  // 대기 중인 텍스트 음성으로 출력
        }
    }
    fun stop() {
        textToSpeech?.stop() // TTS 중지
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
