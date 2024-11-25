package com.example.speechrecognizer.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.speechrecognizer.R
import com.example.speechrecognizer.controller.MainController

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var mainView: MainView
    private lateinit var mainController: MainController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            mainController.startListening(isDeparture)
        } else {
            Log.e(TAG, "Permission denied.")
            mainView.enableButtons()
        }
    }

    private var isDeparture = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        setContentView(R.layout.activity_main)

        mainView = MainView(findViewById(android.R.id.content))
        mainController = MainController(this, mainView)

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
            mainController.startListening(isDeparture)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainController.destroy()
        Log.d(TAG, "SpeechRecognizer and TextToSpeech have been released.")
    }
}
