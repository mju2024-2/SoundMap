package com.example.speechrecognizer.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.*
import com.example.speechrecognizer.model.RecognitionResult

class SpeechRecognitionManager(
    context: Context,
    private val language: String = "ko-KR"
) {
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private var listener: SpeechRecognitionListener? = null
    private val numbersOnlyRegex = Regex("\\D")

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                listener?.onReadyForSpeech()
            }

            override fun onBeginningOfSpeech() {
                listener?.onBeginningOfSpeech()
            }

            override fun onRmsChanged(rmsdB: Float) {
                listener?.onRmsChanged(rmsdB)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                listener?.onBufferReceived()
            }

            override fun onEndOfSpeech() {
                listener?.onEndOfSpeech()
            }

            override fun onError(error: Int) {
                listener?.onError(error)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    val numbersOnly = recognizedText.replace(numbersOnlyRegex, "")
                    val recognitionResult = RecognitionResult(recognizedText, numbersOnly)
                    listener?.onResults(recognitionResult)
                } else {
                    listener?.onResults(null)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                listener?.onPartialResults()
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                listener?.onEvent(eventType)
            }
        })
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        }
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }

    fun setSpeechRecognitionListener(listener: SpeechRecognitionListener) {
        this.listener = listener
    }

    interface SpeechRecognitionListener {
        fun onReadyForSpeech()
        fun onBeginningOfSpeech()
        fun onRmsChanged(rmsdB: Float)
        fun onBufferReceived()
        fun onEndOfSpeech()
        fun onError(error: Int)
        fun onResults(result: RecognitionResult?)
        fun onPartialResults()
        fun onEvent(eventType: Int)
    }
}
