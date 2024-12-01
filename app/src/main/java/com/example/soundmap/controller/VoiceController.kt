package com.example.soundmap.controller

import android.util.Log
import android.content.Context
import android.speech.SpeechRecognizer
import com.example.soundmap.model.SpeechToTextService
import com.example.soundmap.model.TextToSpeechService
import com.example.soundmap.model.RecognitionResult

class VoiceController(
    private val context: Context,
    private val listener: VoiceInputListener
) : SpeechToTextService.SpeechRecognitionListener {

    private val speechToTextService = SpeechToTextService(context, this)
    private val textToSpeechService = TextToSpeechService(context)
    private var isDeparture = true

    fun startListening(isDeparture: Boolean) {
        this.isDeparture = isDeparture
        speechToTextService.startListening()
    }

    override fun onReadyForSpeech() {
        Log.d("SpeechRecognizer", "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d("SpeechRecognizer", "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Log.d("SpeechRecognizer", "onRmsChanged: $rmsdB")
    }

    override fun onBufferReceived() {
        Log.d("SpeechRecognizer", "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.d("SpeechRecognizer", "onEndOfSpeech")
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client-side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech input timeout"
            else -> "Unknown error occurred"
        }
        Log.e("SpeechRecognizer", "onError: $errorMessage")
    }

    override fun onResults(result: RecognitionResult?) {
        if (result != null) {
            Log.d("SpeechRecognizer", "onResults: Recognized text: ${result.recognizedText}")
            Log.d("SpeechRecognizer", "onResults: Extracted numbers: ${result.numbersOnly}")
            listener.onVoiceInputReceived(isDeparture, result.numbersOnly)
            textToSpeechService.speak(result.numbersOnly)
        } else {
            Log.d("SpeechRecognizer", "onResults: No recognition result matched.")
        }
    }

    override fun onPartialResults() {
        Log.d("SpeechRecognizer", "onPartialResults")
    }

    override fun onEvent(eventType: Int) {
        Log.d("SpeechRecognizer", "onEvent: $eventType")
    }

    fun destroy() {
        speechToTextService.destroy()
        textToSpeechService.shutdown()
    }

    interface VoiceInputListener {
        fun onVoiceInputReceived(isDeparture: Boolean, result: String)
    }
}