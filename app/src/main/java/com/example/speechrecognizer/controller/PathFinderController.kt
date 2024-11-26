package com.example.speechrecognizer.controller

import android.content.Context
import android.util.Log
import com.example.speechrecognizer.model.PathFinder
import com.example.speechrecognizer.model.Route
import com.example.speechrecognizer.view.MainView

class PathFinderController(
    private val context: Context,
    private val mainView: MainView
) {
    private val subwayMap = Route()

    init {
        loadRouteData()
    }

    private fun loadRouteData() {
        try {
            val inputStream = context.assets.open("stations.xlsx")
            val workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                if (row.rowNum == 0) continue // 첫 번째 줄은 헤더라면 무시
                val startStation = row.getCell(0)?.numericCellValue?.toInt() ?: continue
                val endStation = row.getCell(1)?.numericCellValue?.toInt() ?: continue
                val distance = row.getCell(2)?.numericCellValue?.toInt() ?: 0
                val cost = row.getCell(3)?.numericCellValue?.toInt() ?: 0
                val time = row.getCell(4)?.numericCellValue?.toInt() ?: 0

                subwayMap.addEdge(startStation, endStation, distance, cost, time)
            }

            workbook.close()
        } catch (e: Exception) {
            Log.e("PathFinderController", "Error loading route data: ${e.message}")
        }
    }

    fun onSearchButtonClicked() {
        val startStation = mainView.startEditText.text.toString().toIntOrNull()
        val endStation = mainView.endEditText.text.toString().toIntOrNull()

        if (startStation == null || endStation == null) {
            Log.e("PathFinderController", "Invalid station number.")
            mainView.displayError("출발역과 도착역을 올바르게 입력해주세요.")
            return
        }

        val pathCost = PathFinder.dijkstra(subwayMap, startStation, endStation, "cost")
        val pathDistance = PathFinder.dijkstra(subwayMap, startStation, endStation, "distance")
        val pathTime = PathFinder.dijkstra(subwayMap, startStation, endStation, "time")

        if (pathCost != null) {
            val costText = """
                최소 비용 경로
                ${pathCost.path.joinToString(" -> ")}
                비용 : ${pathCost.cost.first}
                거리 : ${pathCost.cost.second}
                시간 : ${pathCost.cost.third}
            """.trimIndent()
            mainView.displayCostResult(costText)
        } else {
            mainView.displayCostResult("최소 비용 경로를 찾을 수 없습니다.")
        }

        if (pathDistance != null) {
            val distanceText = """
                최단 거리 경로
                ${pathDistance.path.joinToString(" -> ")}
                비용 : ${pathDistance.cost.first}
                거리 : ${pathDistance.cost.second}
                시간 : ${pathDistance.cost.third}
            """.trimIndent()
            mainView.displayDistanceResult(distanceText)
        } else {
            mainView.displayDistanceResult("최단 거리 경로를 찾을 수 없습니다.")
        }

        if (pathTime != null) {
            val timeText = """
                최소 시간 경로
                ${pathTime.path.joinToString(" -> ")}
                비용 : ${pathTime.cost.first}
                거리 : ${pathTime.cost.second}
                시간 : ${pathTime.cost.third}
            """.trimIndent()
            mainView.displayTimeResult(timeText)
        } else {
            mainView.displayTimeResult("최소 시간 경로를 찾을 수 없습니다.")
        }
    }
}
