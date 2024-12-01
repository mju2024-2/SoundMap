package com.example.soundmap.model

data class Station(
    val station: Int,
    val distance: Int,
    val cost: Int,
    val time: Int,
    var next: Station? = null
)
