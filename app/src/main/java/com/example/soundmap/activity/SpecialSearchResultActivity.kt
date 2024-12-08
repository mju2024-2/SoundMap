package com.example.soundmap.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.soundmap.R
import com.example.soundmap.model.SharedData
import com.example.soundmap.model.dijkstra
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.soundmap.model.TextToSpeechService

class SpecialSearchResultActivity : AppCompatActivity() {
    // 경로 세부 정보를 담는 컨테이너 레이아웃
    private lateinit var routeDetailsContainer: LinearLayout

    // 실시간 UI 업데이트를 위한 핸들러와 실행 가능한 객체
    private lateinit var updateHandler: Handler
    private lateinit var updateRunnable: Runnable
    private val currentTime = LocalTime.now()
    private lateinit var textToSpeechService: TextToSpeechService
    val formatter = DateTimeFormatter.ofPattern("a hh:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_search_result)
        routeDetailsContainer = findViewById(R.id.routeDetailsContainer)
        textToSpeechService = TextToSpeechService(this)
        //값들 받아오기
        val startStation = intent.getStringExtra("startStation")?.toIntOrNull() ?: 0
        val endStation = intent.getStringExtra("endStation")?.toIntOrNull() ?: 0

        val (disPath, distance) = dijkstra(SharedData.subwayMap, startStation, endStation, "distance") ?: error("경로를 찾을 수 없습니다.")
        val (dis_path, dis_line) = disPath
        val (dis_totalCost, dis_totalDis, dis_totalTime) = distance
        var dis_count = 0
        for(i in dis_path){
            dis_count++
        }
        dis_count = dis_count - 1

        val speak1 = speakRouteInfo(startStation.toString(), endStation.toString(), dis_totalTime, dis_totalDis, dis_totalCost)
        val speak2 = RouteSpeech(dis_line, dis_path)
        textToSpeechService.speak(speak1 + speak2)
        updateUI(startStation.toString(), endStation.toString(), dis_totalTime, dis_totalDis, dis_totalCost, dis_count)
        populateRouteDetails(dis_path, dis_line)
    }
    private fun speakRouteInfo(startStation: String, endStation: String, dis_totalTime: Int, dis_totalDis: Int, dis_totalcost: Int):String {
        // 소요시간을 분과 초로 변환
        val hour = dis_totalTime / 3600
        val minute = (dis_totalTime % 3600) / 60
        val second = dis_totalTime % 60

        // 시간 출력 형식 결정
        val timeString = if (hour > 0) {
            "${hour}시간 ${minute}분 ${second}초"  // 1시간 이상일 때
        } else {
            "$minute 분 $second 초"  // 1시간 미만일 때
        }

//        val arrivalTime = currentTime.plusSeconds(dis_totalTime.toLong()).format(formatter).replace("AM", "오전").replace("PM", "오후")
        val arrivalTime = currentTime.plusSeconds(dis_totalTime.toLong()).format(formatter)
        val timeParts = arrivalTime.split(":")
        val formattedArrivalTime = "${timeParts[0]}시 ${timeParts[1]}분 ${timeParts[2]}초"
        // 음성으로 안내할 텍스트
        val speechText = "출발 역은 $startStation 번 역, 도착 역은 $endStation 번 역입니다. 소요 시간은 $timeString, 총 거리는 $dis_totalDis 미터," +
                "총 비용은 $dis_totalcost 원 입니다. 도착예정시각은 $formattedArrivalTime 입니다."

        // 음성으로 안내
        return speechText
    }
    private fun RouteSpeech(line : List<Int>, path : List<Int>): String{
        var speechRoute : String
        speechRoute = "지하철 경로는 ${path[0]}번 역에서 ${line[0]} 호선 지하철을 타고,"
        var prevline = line[0]
        val route_line = line + line.last()
        Log.d("route", "route : $path, line : $route_line")
        for (i in 0 until path.size){
            if(prevline != route_line[i]){
                speechRoute = speechRoute + "${path[i]}번역까지 이동하시고 ${path[i]}번 역에서 ${route_line[i]}호선으로 갈아타셔서,"
            }
            else{
                if (i == 0){
                    continue
                }else {
                    speechRoute = speechRoute + "${path[i]}번역, "
                }
            }
            prevline = route_line[i]
        }
        speechRoute = speechRoute + "으로 이동하세요"
        return speechRoute
    }



    // 호선에 맞는 색상을 가져오는 함수
    private fun getLineColor(lineNumber: Int): String {
        return when (lineNumber) {
            1 -> "#00b050"
            2 -> "#002060"
            3 -> "#953735"
            4 -> "#FF0000"
            5 -> "#8AABD2"
            6 -> "#FFC000"
            7 -> "#92D050"
            8 -> "#00B0F0"
            else -> "#808080" // 기본 색상
        }
    }

    /**
     * 전달받은 노선 데이터를 UI로 변환하여 표시합니다.
     * - 노선별로 역과 선의 구성을 동적으로 생성.
     */
    //지하철 상세 노선 그리기
    private fun populateRouteDetails(route: List<Int>, line: List<Int>) {

        val allStations = mutableListOf<String>()
        val allLineColors = mutableListOf<String>()

        // 역 이름 생성
        for (station in route) {
            allStations.add("$station 역")
        }
        // 노선 색상 생성
        for (lineCode in line) {
            allLineColors.add(getLineColor(lineCode)) // 노선 코드에 따라 색상 가져오기
        }

        val routeLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }

        var previousLine = -1 // 이전 노선 추적

        for ((index, stationName) in allStations.withIndex()) {
            // 현재 노선 결정
            val currentLine = if (index < line.size) line[index] else -1

            // 각 역에 대한 레이아웃 생성
            val stationRow = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val lineAndCircleContainer = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    50, LinearLayout.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
            }

            val stationIcon = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                background = ContextCompat.getDrawable(this@SpecialSearchResultActivity, R.drawable.circle_icon)
            }

            if (index > 0) {
                val lineAbove = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(8, 50).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    setBackgroundColor(Color.parseColor(allLineColors[index - 1]))
                }
                lineAndCircleContainer.addView(lineAbove)
            } else {
                lineAndCircleContainer.addView(View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(8, 25)
                })
            }

            lineAndCircleContainer.addView(stationIcon)

            if (index < allStations.size - 1) {
                val lineBelow = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(8, 50).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    setBackgroundColor(Color.parseColor(allLineColors.getOrElse(index) { "#FFFFFF" }))
                }
                lineAndCircleContainer.addView(lineBelow)
            } else {
                lineAndCircleContainer.addView(View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(8, 25)
                })
            }

            val stationNameView = TextView(this).apply {
                text = stationName
                textSize = 14f
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1f
                )
                gravity = Gravity.CENTER_VERTICAL
                setPadding(16, 0, 0, 0)
            }

            val lineNameView = TextView(this).apply {
                text = if (currentLine != previousLine && currentLine != -1) "${currentLine}호선" else ""
                textSize = 14f
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                gravity = Gravity.CENTER_VERTICAL
                setPadding(16, 0, 0, 0)
            }

            stationRow.addView(lineAndCircleContainer)
            stationRow.addView(stationNameView)
            stationRow.addView(lineNameView)

            routeLayout.addView(stationRow)

            // 이전 노선 갱신
            if (currentLine != -1) previousLine = currentLine
        }

        routeDetailsContainer.addView(routeLayout)
    }
    @SuppressLint("SetTextI18n")
    private fun updateUI(startStation : String, endStation : String, time : Int, distance : Int, cost : Int, stationCount : Int) {
        val arrivalTime = currentTime.plusSeconds(time.toLong())
        val hour = time / 3600
        val minute = (time % 3600) / 60
        val second = time % 60

        // 시간 출력 형식 결정
        val timeString = if (hour > 0) {
            "${hour}시간 ${minute}분 ${second}초"  // 1시간 이상일 때
        } else {
            "$minute 분 $second 초"  // 1시간 미만일 때
        }
        findViewById<TextView>(R.id.startStation).text = startStation + "번 역 "
        findViewById<TextView>(R.id.endStation).text = endStation + "번 역 "
        findViewById<TextView>(R.id.countStation).text = "    " + stationCount + "개역 이동"
        findViewById<TextView>(R.id.time).text = timeString
        findViewById<TextView>(R.id.distance).text = distance.toString() + "m"
        findViewById<TextView>(R.id.cost).text = cost.toString() + "원"
        findViewById<TextView>(R.id.arrivalTime).text = arrivalTime.format(formatter)

//        findViewById<TextView>(R.id.arrivalTime).text = arrivalTime.format(formatter).replace("AM", "오전").replace("PM", "오후")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (::updateHandler.isInitialized) {
            updateHandler.removeCallbacks(updateRunnable)
        }
        if (::textToSpeechService.isInitialized) {
            textToSpeechService.stop() // TTS 중지 메서드 호출
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::updateHandler.isInitialized) {
            updateHandler.removeCallbacks(updateRunnable)
        }
    }
}