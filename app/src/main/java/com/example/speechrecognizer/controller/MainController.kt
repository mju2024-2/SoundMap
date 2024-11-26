package com.example.speechrecognizer.controller

import android.content.Context
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.speechrecognizer.model.RecognitionResult
import com.example.speechrecognizer.model.SpeechToTextService
import com.example.speechrecognizer.model.TextToSpeechService
import com.example.speechrecognizer.view.MainView

class MainController(
    private val context: Context,
    private val mainView: MainView
) : SpeechToTextService.SpeechRecognitionListener {

    private val speechRecognitionManager = SpeechToTextService(context, this)
    private val textToSpeechManager = TextToSpeechService(context)
    private var isDeparture = true

    fun startListening(isDeparture: Boolean) {
        this.isDeparture = isDeparture
        speechRecognitionManager.startListening()
    }

    override fun onReadyForSpeech() {
        Log.d("SpeechRecognizer", "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d("SpeechRecognizer", "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d("SpeechRecognizer", "onRmsChanged: $rmsdB")
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
        mainView.enableButtons()
    }

    override fun onResults(result: RecognitionResult?) {
        if (result != null) {
            Log.d("SpeechRecognizer", "onResults: Recognized text: ${result.recognizedText}")
            Log.d("SpeechRecognizer", "onResults: Extracted numbers: ${result.numbersOnly}")

            if (isDeparture) {
                mainView.startEditText.setText(result.numbersOnly)
            } else {
                mainView.endEditText.setText(result.numbersOnly)
            }
            textToSpeechManager.speak(result.numbersOnly)
        } else {
            Log.d("SpeechRecognizer", "onResults: No recognition result matched.")
        }
        mainView.enableButtons()
    }

    override fun onPartialResults() {
        Log.d("SpeechRecognizer", "onPartialResults")
    }

    override fun onEvent(eventType: Int) {
        Log.d("SpeechRecognizer", "onEvent: $eventType")
    }

    fun destroy() {
        speechRecognitionManager.destroy()
        textToSpeechManager.shutdown()
    }
}
