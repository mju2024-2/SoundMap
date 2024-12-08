package com.example.soundmap.model

import android.content.Context
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

//지하철 역 정보 저장 노드
data class StationNode (
    val station: Int,
    val distance: Int,
    val cost: Int,
    val time: Int,
    val type: Int,
    var next: StationNode? = null
)
//지하철 역 정보를 엑셀파일에서 읽어오는 함수
fun readStation(context: Context) {
    val inputStream: InputStream = context.assets.open("stations.xlsx")
    val workbook = XSSFWorkbook(inputStream)
    val sheet = workbook.getSheetAt(0)

    for (row in sheet) {
        if (row.rowNum == 0) continue // 첫 번째 줄은 헤더라면 무시
        val startStation = row.getCell(0)?.numericCellValue?.toInt() ?: continue    //출발역
        val endStation = row.getCell(1)?.numericCellValue?.toInt() ?: continue  //도착역
        val time = row.getCell(2)?.numericCellValue?.toInt() ?: 0   //시간
        val distance = row.getCell(3)?.numericCellValue?.toInt() ?: 0   //거리
        val cost = row.getCell(4)?.numericCellValue?.toInt() ?: 0   //비용
        val type = row.getCell(5)?.numericCellValue?.toInt() ?: 0   //호선 종류
        SharedData.subwayMap.addEdge(startStation, endStation, distance, cost, time, type)
    }

    workbook.close()
}
