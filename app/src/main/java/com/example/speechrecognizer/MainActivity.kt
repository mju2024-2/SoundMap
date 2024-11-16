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
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // XML 레이아웃 설정

        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
        }

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        // RecognitionListener 정의 및 설정
        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "onReadyForSpeech: Speech recognizer is ready.")
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
            }

            override fun onResults(results: Bundle?) {
                Log.d("SpeechRecognizer", "onResults: Recognition results received.")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizer", "onResults: Recognized text: $recognizedText")

                    // TextView 업데이트 코드 추가
                    runOnUiThread {
                        val noteTextView = findViewById<TextView>(R.id.noteTextView)
                        noteTextView.text = recognizedText
                    }
                } else {
                    Log.d("SpeechRecognizer", "onResults: No recognition result matched.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SpeechRecognizer", "onPartialResults: Partial recognition results received.")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizer", "onEvent: Event occurred. EventType: $eventType")
            }
        }

        // SpeechRecognizer에 Listener 연결
        speechRecognizer.setRecognitionListener(recognitionListener)

        // 음성 인식 시작 버튼을 예로 들어 설정
        findViewById<Button>(R.id.startListeningButton).setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }

            speechRecognizer.startListening(intent)
        }

    }
}