package com.example.soundmap.model

import com.example.soundmap.model.Route
import com.example.soundmap.model.Station
import java.util.PriorityQueue

data class PathResult(
    val path: List<Int>,
    val cost: Triple<Int, Int, Int>
)

object PathFinder {

    fun dijkstra(
        subwayMap: Route,
        start: Int,
        end: Int,
        weightType: String
    ): PathResult? {
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })
        priorityQueue.add(Pair(0, start))

        val distances = mutableMapOf<Int, Int>()
        distances[start] = 0
        val previousNodes = mutableMapOf<Int, Int?>()
        previousNodes[start] = null

        while (priorityQueue.isNotEmpty()) {
            val (currentDist, currentStation) = priorityQueue.poll()

            if (currentStation == end) {
                val path = mutableListOf<Int>()
                var totalDistance = 0
                var totalCost = 0
                var totalTime = 0
                var current = currentStation

                while (current != null) {
                    path.add(0, current)
                    val prevStation = previousNodes[current]
                    if (prevStation != null) {
                        for (neighbor in subwayMap.getNeighbors(prevStation)) {
                            if (neighbor.station == current) {
                                totalDistance += neighbor.distance
                                totalCost += neighbor.cost
                                totalTime += neighbor.time
                                break
                            }
                        }
                    }
                    current = prevStation ?: break
                }
                return PathResult(path, Triple(totalCost, totalDistance, totalTime))
            }

            for (neighbor in subwayMap.getNeighbors(currentStation)) {
                val weight = when (weightType) {
                    "cost" -> neighbor.cost
                    "distance" -> neighbor.distance
                    "time" -> neighbor.time
                    else -> throw IllegalArgumentException("Invalid weight type.")
                }

                val newDist = currentDist + weight

                if (distances[neighbor.station] == null || newDist < distances[neighbor.station]!!) {
                    distances[neighbor.station] = newDist
                    previousNodes[neighbor.station] = currentStation
                    priorityQueue.add(Pair(newDist, neighbor.station))
                }
            }
        }
        return null
    }
}
