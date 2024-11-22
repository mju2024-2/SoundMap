package com.example.speechrecognizer.view

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.speechrecognizer.R

class MainView(rootView: View) {
    val startListeningButtonDeparture: Button = rootView.findViewById(R.id.startListeningButtonDeparture)
    val startListeningButtonDestination: Button = rootView.findViewById(R.id.startListeningButtonDestination)
    val noteTextViewDeparture: TextView = rootView.findViewById(R.id.noteTextViewDeparture)
    val noteTextViewDestination: TextView = rootView.findViewById(R.id.noteTextViewDestination)

    fun disableButtons() {
        startListeningButtonDeparture.isEnabled = false
        startListeningButtonDeparture.alpha = 0.5f
        startListeningButtonDestination.isEnabled = false
        startListeningButtonDestination.alpha = 0.5f
    }

    fun enableButtons() {
        startListeningButtonDeparture.isEnabled = true
        startListeningButtonDeparture.alpha = 1.0f
        startListeningButtonDestination.isEnabled = true
        startListeningButtonDestination.alpha = 1.0f
    }
}
