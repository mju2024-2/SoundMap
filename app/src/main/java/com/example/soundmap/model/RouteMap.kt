package com.example.soundmap.model

import java.util.HashMap

class RouteMap {
    val map: HashMap<Int, StationNode> = HashMap()

    fun addEdge(startStation: Int, endStation: Int, distance: Int, cost: Int, time: Int, type: Int) {
        val newNode = StationNode(endStation, distance, cost, time, type)
        if (map[startStation] == null) {
            map[startStation] = newNode
        } else {
            var current = map[startStation]
            while (current?.next != null) {
                current = current.next
            }
            current?.next = newNode
        }

        val reverseNode = StationNode(startStation, distance, cost, time, type)
        if (map[endStation] == null) {
            map[endStation] = reverseNode
        } else {
            var currentReverse = map[endStation]
            while (currentReverse?.next != null) {
                currentReverse = currentReverse.next
            }
            currentReverse?.next = reverseNode
        }
    }

    fun getNeighbors(station: Int): List<StationNode> {
        val neighbors = mutableListOf<StationNode>()
        var current = map[station]
        while (current != null) {
            neighbors.add(current)
            current = current.next
        }
        return neighbors
    }
}

object SharedData {
    val subwayMap = RouteMap()
}
