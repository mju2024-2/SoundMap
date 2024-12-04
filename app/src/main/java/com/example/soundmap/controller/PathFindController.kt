package com.example.soundmap.controller

import android.content.Context
import android.util.Log
import com.example.soundmap.model.SharedData
import com.example.soundmap.model.dijkstra

class PathFindController(
    private val context: Context,
    private val listener: PathFindListener
) {
    private val subwayMap = SharedData.subwayMap

    init {
        loadRouteData()
    }

    private fun loadRouteData() {
        try {
            val inputStream = context.assets.open("stations.xlsx")
            val workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                if (row.rowNum == 0) continue
                val startStation = row.getCell(0)?.numericCellValue?.toInt() ?: continue
                val endStation = row.getCell(1)?.numericCellValue?.toInt() ?: continue
                val distance = row.getCell(2)?.numericCellValue?.toInt() ?: 0
                val cost = row.getCell(3)?.numericCellValue?.toInt() ?: 0
                val time = row.getCell(4)?.numericCellValue?.toInt() ?: 0
                val type = row.getCell(5)?.numericCellValue?.toInt() ?: 0

                subwayMap.addEdge(startStation, endStation, distance, cost, time, type)
            }

            workbook.close()
        } catch (e: Exception) {
            Log.e("PathFinderController", "Error loading route data: ${e.message}")
        }
    }

    fun onSearchButtonClicked(startStation: String?, endStation: String?) {
        if (startStation.isNullOrBlank() || endStation.isNullOrBlank()) {
            listener.displayError("출발역과 도착역을 올바르게 입력해주세요.")
            return
        }

        val start = startStation.toIntOrNull()
        val end = endStation.toIntOrNull()

        if (start == null || end == null) {
            listener.displayError("역 번호는 숫자로 입력해야 합니다.")
            return
        }

        val costPath = findPath(start, end, "cost")
        val distancePath = findPath(start, end, "distance")
        val timePath = findPath(start, end, "time")

        listener.displayCostResult(costPath)
        listener.displayDistanceResult(distancePath)
        listener.displayTimeResult(timePath)
    }

    private fun findPath(start: Int, end: Int, weightType: String): String {
        val result = dijkstra(subwayMap, start, end, weightType)
        return if (result != null) {
            val (pathAndLine, costDetails) = result
            val (path, line) = pathAndLine
            val (cost, distance, time) = costDetails

            """
            $weightType 기준 최적 경로
            경로: ${path.joinToString(" -> ")} (호선: ${line.joinToString(", ")})
            비용: $cost
            거리: $distance
            시간: $time
            """.trimIndent()
        } else {
            "$weightType 기준 최적 경로를 찾을 수 없습니다."
        }
    }

    interface PathFindListener {
        fun displayError(message: String)
        fun displayCostResult(result: String)
        fun displayDistanceResult(result: String)
        fun displayTimeResult(result: String)
    }
}
