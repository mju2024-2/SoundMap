package com.example.soundmap.model

import java.util.HashMap

class Route {
    private val map: HashMap<Int, Station> = HashMap()

    fun addEdge(startStation: Int, endStation: Int, distance: Int, cost: Int, time: Int) {
        val newStation = Station(endStation, distance, cost, time)
        if (map[startStation] == null) {
            map[startStation] = newStation
        } else {
            var current = map[startStation]
            while (current?.next != null) {
                current = current.next
            }
            current?.next = newStation
        }

        val reverseStation = Station(startStation, distance, cost, time)
        if (map[endStation] == null) {
            map[endStation] = reverseStation
        } else {
            var currentReverse = map[endStation]
            while (currentReverse?.next != null) {
                currentReverse = currentReverse.next
            }
            currentReverse?.next = reverseStation
        }
    }

    fun getNeighbors(station: Int): List<Station> {
        val neighbors = mutableListOf<Station>()
        var current = map[station]
        while (current != null) {
            neighbors.add(current)
            current = current.next
        }
        return neighbors
    }
}
