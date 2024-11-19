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

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var speechToTextDeparture: SpeechRecognizer
    private lateinit var speechToTextDestination: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        setContentView(R.layout.activity_main)

        val startListeningButtonDeparture: Button = findViewById(R.id.startListeningButtonDeparture)
        val startListeningButtonDestination: Button = findViewById(R.id.startListeningButtonDestination)
        val noteTextViewDeparture: TextView = findViewById(R.id.noteTextViewDeparture)
        val noteTextViewDestination: TextView = findViewById(R.id.noteTextViewDestination)

        speechToTextDeparture = SpeechRecognizer.createSpeechRecognizer(this)
        speechToTextDestination = SpeechRecognizer.createSpeechRecognizer(this)

        val recognitionListenerDeparture = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizerDeparture", "onReadyForSpeech: Speech recognizer is ready, button disabled.")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizerDeparture", "onBeginningOfSpeech: User started speaking.")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d("SpeechRecognizerDeparture", "onRmsChanged: Voice volume changed. RMS: $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SpeechRecognizerDeparture", "onBufferReceived: Audio buffer received.")
            }

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizerDeparture", "onEndOfSpeech: User stopped speaking.")
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
                Log.e("SpeechRecognizerDeparture", "onError: $errorMessage")
                startListeningButtonDeparture.isEnabled = true
                startListeningButtonDeparture.alpha = 1.0f
                startListeningButtonDestination.isEnabled = true
                startListeningButtonDestination.alpha = 1.0f
            }

            override fun onResults(results: Bundle?) {
                Log.d("SpeechRecognizerDeparture", "onResults: Recognition results received.")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizerDeparture", "onResults: Recognized text: $recognizedText")

                    runOnUiThread {
                        noteTextViewDeparture.text = recognizedText
                    }
                } else {
                    Log.d("SpeechRecognizerDeparture", "onResults: No recognition result matched.")
                }
                startListeningButtonDeparture.isEnabled = true
                startListeningButtonDeparture.alpha = 1.0f
                startListeningButtonDestination.isEnabled = true
                startListeningButtonDestination.alpha = 1.0f
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SpeechRecognizerDeparture", "onPartialResults: Partial recognition results received.")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizerDeparture", "onEvent: Event occurred. EventType: $eventType")
            }
        }

        val recognitionListenerDestination = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizerDestination", "onReadyForSpeech: Speech recognizer is ready, button disabled.")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizerDestination", "onBeginningOfSpeech: User started speaking.")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d("SpeechRecognizerDestination", "onRmsChanged: Voice volume changed. RMS: $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SpeechRecognizerDestination", "onBufferReceived: Audio buffer received.")
            }

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizerDestination", "onEndOfSpeech: User stopped speaking.")
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
                Log.e("SpeechRecognizerDestination", "onError: $errorMessage")
                startListeningButtonDeparture.isEnabled = true
                startListeningButtonDeparture.alpha = 1.0f
                startListeningButtonDestination.isEnabled = true
                startListeningButtonDestination.alpha = 1.0f
            }

            override fun onResults(results: Bundle?) {
                Log.d("SpeechRecognizerDestination", "onResults: Recognition results received.")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizerDestination", "onResults: Recognized text: $recognizedText")

                    runOnUiThread {
                        noteTextViewDestination.text = recognizedText
                    }
                } else {
                    Log.d("SpeechRecognizerDestination", "onResults: No recognition result matched.")
                }
                startListeningButtonDeparture.isEnabled = true
                startListeningButtonDeparture.alpha = 1.0f
                startListeningButtonDestination.isEnabled = true
                startListeningButtonDestination.alpha = 1.0f
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SpeechRecognizerDestination", "onPartialResults: Partial recognition results received.")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizerDestination", "onEvent: Event occurred. EventType: $eventType")
            }
        }

        speechToTextDeparture.setRecognitionListener(recognitionListenerDeparture)
        speechToTextDestination.setRecognitionListener(recognitionListenerDestination)

        startListeningButtonDeparture.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
            } else {
                startListeningButtonDeparture.isEnabled = false
                startListeningButtonDeparture.alpha = 0.5f
                startListeningButtonDestination.isEnabled = false
                startListeningButtonDestination.alpha = 0.5f
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                }

                speechToTextDeparture.startListening(intent)
            }
        }

        startListeningButtonDestination.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
            } else {
                startListeningButtonDeparture.isEnabled = false
                startListeningButtonDeparture.alpha = 0.5f
                startListeningButtonDestination.isEnabled = false
                startListeningButtonDestination.alpha = 0.5f
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                }

                speechToTextDestination.startListening(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        speechToTextDeparture.destroy()
        speechToTextDestination.destroy()
        Log.d("SpeechRecognizer", "SpeechRecognizers destroyed.")
    }
}
