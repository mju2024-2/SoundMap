package com.example.speechrecognizer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : ComponentActivity() {
    private lateinit var speechToText: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startListeningButton: Button = findViewById(R.id.startListeningButton)
        speechToText = SpeechRecognizer.createSpeechRecognizer(this)

        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "onReadyForSpeech: Speech recognizer is ready, button disabled.")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "onBeginningOfSpeech: User started speaking.")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d("SpeechRecognizer", "onRmsChanged: Voice volume changed. RMS: $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SpeechRecognizer", "onBufferReceived: Audio buffer received.")
            }

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "onEndOfSpeech: User stopped speaking.")
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
                startListeningButton.isEnabled = true
                startListeningButton.alpha = 1.0f
            }

            override fun onResults(results: Bundle?) {
                Log.d("SpeechRecognizer", "onResults: Recognition results received.")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizer", "onResults: Recognized text: $recognizedText")

                    runOnUiThread {
                        val noteTextView = findViewById<TextView>(R.id.noteTextView)
                        noteTextView.text = recognizedText
                    }
                } else {
                    Log.d("SpeechRecognizer", "onResults: No recognition result matched.")
                }
                startListeningButton.isEnabled = true
                startListeningButton.alpha = 1.0f
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SpeechRecognizer", "onPartialResults: Partial recognition results received.")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizer", "onEvent: Event occurred. EventType: $eventType")
            }

        }

        speechToText.setRecognitionListener(recognitionListener)
        startListeningButton.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
            } else {
                startListeningButton.isEnabled = false
                startListeningButton.alpha = 0.5f
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                }

                speechToText.startListening(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechToText.destroy()
        Log.d("SpeechRecognizer", "SpeechRecognizer destroyed.")
    }

}