package com.example.speechrecognizer.controller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import com.example.speechrecognizer.R
import com.example.speechrecognizer.model.RecognitionResult
import com.example.speechrecognizer.view.MainView

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var speechRecognitionManager: SpeechRecognitionManager
    private lateinit var textToSpeechManager: TextToSpeechManager
    private lateinit var mainView: MainView

    private var isDeparture = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startListening()
        } else {
            Log.e(TAG, "Permission denied.")
            mainView.enableButtons()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        setContentView(R.layout.activity_main)

        mainView = MainView(findViewById(android.R.id.content))

        textToSpeechManager = TextToSpeechManager(this)

        speechRecognitionManager = SpeechRecognitionManager(this)
        speechRecognitionManager.setSpeechRecognitionListener(object : SpeechRecognitionManager.SpeechRecognitionListener {
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
                        mainView.noteTextViewDeparture.text = result.numbersOnly
                    } else {
                        mainView.noteTextViewDestination.text = result.numbersOnly
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
        })

        mainView.startListeningButtonDeparture.setOnClickListener {
            isDeparture = true
            mainView.disableButtons()
            checkPermissionAndStartListening()
        }

        mainView.startListeningButtonDestination.setOnClickListener {
            isDeparture = false
            mainView.disableButtons()
            checkPermissionAndStartListening()
        }
    }

    private fun checkPermissionAndStartListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            startListening()
        }
    }

    private fun startListening() {
        speechRecognitionManager.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechManager.shutdown()
        speechRecognitionManager.destroy()
        Log.d(TAG, "SpeechRecognizer and TextToSpeech have been released.")
    }
}
