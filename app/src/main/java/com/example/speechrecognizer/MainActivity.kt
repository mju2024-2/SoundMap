package com.example.speechrecognizer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.speech.tts.TextToSpeech
import java.util.Locale

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private var ttsInitialized = false
    private var isDeparture = true
    private val numbersOnlyRegex = Regex("\\D")

    private lateinit var startListeningButtonDeparture: Button
    private lateinit var startListeningButtonDestination: Button
    private lateinit var noteTextViewDeparture: TextView
    private lateinit var noteTextViewDestination: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startListening()
        } else {
            Log.e(TAG, "Permission denied.")
            startListeningButtonDeparture.isEnabled = true
            startListeningButtonDeparture.alpha = 1.0f
            startListeningButtonDestination.isEnabled = true
            startListeningButtonDestination.alpha = 1.0f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        setContentView(R.layout.activity_main)

        startListeningButtonDeparture = findViewById(R.id.startListeningButtonDeparture)
        startListeningButtonDestination = findViewById(R.id.startListeningButtonDestination)
        noteTextViewDeparture = findViewById(R.id.noteTextViewDeparture)
        noteTextViewDestination = findViewById(R.id.noteTextViewDestination)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "onReadyForSpeech: Speech recognizer is ready, buttons disabled.")
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
                startListeningButtonDeparture.isEnabled = true
                startListeningButtonDeparture.alpha = 1.0f
                startListeningButtonDestination.isEnabled = true
                startListeningButtonDestination.alpha = 1.0f
            }

            override fun onResults(results: Bundle?) {
                Log.d("SpeechRecognizer", "onResults: Recognition results received.")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizer", "onResults: Recognized text: $recognizedText")

                    val numbersOnly = recognizedText.replace(numbersOnlyRegex, "")
                    Log.d("SpeechRecognizer", "onResults: Extracted numbers: $numbersOnly")

                    if (isDeparture) {
                        noteTextViewDeparture.text = numbersOnly
                    } else {
                        noteTextViewDestination.text = numbersOnly
                    }

                    if (ttsInitialized) {
                        textToSpeech.speak(numbersOnly, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                } else {
                    Log.d("SpeechRecognizer", "onResults: No recognition result matched.")
                }
                startListeningButtonDeparture.isEnabled = true
                startListeningButtonDeparture.alpha = 1.0f
                startListeningButtonDestination.isEnabled = true
                startListeningButtonDestination.alpha = 1.0f
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SpeechRecognizer", "onPartialResults: Partial recognition results received.")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizer", "onEvent: Event occurred. EventType: $eventType")
            }
        })

        initializeTextToSpeech()

        startListeningButtonDeparture.setOnClickListener {
            isDeparture = true
            startListeningButtonDeparture.isEnabled = false
            startListeningButtonDeparture.alpha = 0.5f
            startListeningButtonDestination.isEnabled = false
            startListeningButtonDestination.alpha = 0.5f
            checkPermissionAndStartListening()
        }

        startListeningButtonDestination.setOnClickListener {
            isDeparture = false
            startListeningButtonDeparture.isEnabled = false
            startListeningButtonDeparture.alpha = 0.5f
            startListeningButtonDestination.isEnabled = false
            startListeningButtonDestination.alpha = 0.5f
            checkPermissionAndStartListening()
        }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS: Language not supported.")
                } else {
                    ttsInitialized = true
                    Log.d(TAG, "TTS: Initialization successful.")
                }
            } else {
                Log.e(TAG, "TTS: Initialization failed.")
            }
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
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }
        speechRecognizer.startListening(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        if (this::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        Log.d(TAG, "SpeechRecognizer and TextToSpeech have been released.")
    }
}
