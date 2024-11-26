package com.example.speechrecognizer.view

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.speechrecognizer.R

class MainView(rootView: View) {
    val startEditText: EditText = rootView.findViewById(R.id.start)
    val endEditText: EditText = rootView.findViewById(R.id.end)
    val startListeningButtonDeparture: Button = rootView.findViewById(R.id.startListeningButtonDeparture)
    val startListeningButtonDestination: Button = rootView.findViewById(R.id.startListeningButtonDestination)
    val searchButton: Button = rootView.findViewById(R.id.search)
    val costResultTextView: TextView = rootView.findViewById(R.id.cost_result)
    val distanceResultTextView: TextView = rootView.findViewById(R.id.distance_result)
    val timeResultTextView: TextView = rootView.findViewById(R.id.time_result)

    fun displayCostResult(text: String) {
        costResultTextView.text = text
    }

    fun displayDistanceResult(text: String) {
        distanceResultTextView.text = text
    }

    fun displayTimeResult(text: String) {
        timeResultTextView.text = text
    }

    fun displayError(message: String) {
        // Toast?
    }

    fun disableButtons() {
        startListeningButtonDeparture.isEnabled = false
        startListeningButtonDeparture.alpha = 0.5f
        startListeningButtonDestination.isEnabled = false
        startListeningButtonDestination.alpha = 0.5f
        searchButton.isEnabled = false
        searchButton.alpha = 0.5f
    }

    fun enableButtons() {
        startListeningButtonDeparture.isEnabled = true
        startListeningButtonDeparture.alpha = 1.0f
        startListeningButtonDestination.isEnabled = true
        startListeningButtonDestination.alpha = 1.0f
        searchButton.isEnabled = true
        searchButton.alpha = 1.0f
    }
}
