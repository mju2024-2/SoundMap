package com.example.soundmap.model

import java.util.PriorityQueue
import android.util.Log

fun dijkstra(subwayMap: RouteMap, start: Int, end: Int, weightType: String): Pair<Pair<List<Int>, List<Int>>, Triple<Int, Int, Int>>? {
    val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })
    priorityQueue.add(Pair(0, start))

    val distances = mutableMapOf(start to 0)
    val previousNodes = mutableMapOf(start to null as Int?)
    val previousLines = mutableMapOf(start to null as Int?) // 역에 대한 호선 정보 저장

    while (priorityQueue.isNotEmpty()) {
        val (currentDist, currentStation) = priorityQueue.poll()

        if (currentStation == end) {
            val path = mutableListOf<Int>()
            val line = mutableListOf<Int>()
            var totalDistance = 0
            var totalCost = 0
            var totalTime = 0
            var current = currentStation

            // 경로를 추적하여 path와 line 리스트에 동시에 추가
            while (current != null) {
                path.add(0, current) // 역을 맨 앞에 추가하여 경로 추적
                val prevStation = previousNodes[current]
                val lineType = previousLines[current] // 이전 호선 가져오기

                if (prevStation != null) {
                    // 이전 역에서 현재 역으로 이어지는 호선 구하기
                    for (neighbor in subwayMap.getNeighbors(prevStation)) {
                        if (neighbor.station == current) {
                            totalDistance += neighbor.distance
                            totalCost += neighbor.cost
                            totalTime += neighbor.time
                            line.add(0, neighbor.type) // 호선 추가
                            break
                        }
                    }
                }
                previousLines[current] = lineType // 호선 정보 업데이트
                current = prevStation ?: break
            }

            // 최단 경로와 관련된 로그 출력
            Log.d("Dijkstra", "최단 경로: ${path}, 호선: ${line}")

            // 최단 경로와 관련된 다른 정보를 반환
            return Pair(Pair(path, line), Triple(totalCost, totalDistance, totalTime))
        }

        // 인접한 역들을 큐에 추가하여 경로를 탐색
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
                previousLines[neighbor.station] = neighbor.type // 호선 정보 저장
                priorityQueue.add(Pair(newDist, neighbor.station))
            }
        }
    }

    // 경로가 없을 경우
    Log.d("Dijkstra", "경로를 찾을 수 없습니다.")
    return null
}

