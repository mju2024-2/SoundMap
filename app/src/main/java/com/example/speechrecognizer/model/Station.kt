package com.example.speechrecognizer.model

data class station(
    val station: Int,
    val distance: Int,
    val cost: Int,
    val time: Int,
    var next: station? = null
)
