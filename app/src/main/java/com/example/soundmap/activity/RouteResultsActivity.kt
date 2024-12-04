package com.example.soundmap.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import com.example.soundmap.model.RouteDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.soundmap.model.SharedData
import com.example.soundmap.model.dijkstra
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * `RouteResultsActivity` 클래스
 * - 사용자가 검색한 경로의 결과를 표시하는 Activity.
 * - 검색 결과를 박스로 표시하고, 즐겨찾기 추가/삭제, 실시간 업데이트 기능 제공.
 */
class RouteResultsActivity : AppCompatActivity() {

    // 경로 세부 정보를 담는 리스트 (샘플 데이터로 초기화)
    private lateinit var routeDetailsList: MutableList<RouteDetails>

    // UI 실시간 업데이트를 위한 핸들러와 실행 가능한 객체
    private lateinit var updateHandler: Handler
    private lateinit var updateRunnable: Runnable

    // SharedPreferences를 사용해 최근 검색 기록 저장
    private val sharedPreferences by lazy {
        getSharedPreferences("recentSearches", Context.MODE_PRIVATE)
    }

    // 즐겨찾기 데이터 (출발역과 도착역을 쌍으로 저장)
    private val favoriteSearches = mutableListOf<Pair<String, String>>()

    /**
     * Activity 생성 시 호출되는 메서드.
     * - 기본 UI 초기화 및 경로 데이터를 설정.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_results)
        // UI 요소 초기화
        val favoriteStarButton: ImageButton = findViewById(R.id.favoriteStarButton)
        val searchResultTitle: TextView = findViewById(R.id.searchResultTitle)

        // Intent로 전달받은 출발역 도착역 가져오기
        val startStation = intent.getStringExtra("startStation") ?: "출발역"
        val endStation = intent.getStringExtra("endStation") ?: "도착역"

        //최단경로 계산
        val (costPath, cost ) = dijkstra(SharedData.subwayMap, startStation.toInt(), endStation.toInt(), "cost") ?: error("경로를 찾을 수 없습니다.")
        val (cost_path, cost_line) = costPath
        val (cost_totalCost, cost_totalDis, cost_totalTime) = cost
        val (disPath, distance) = dijkstra(SharedData.subwayMap, startStation.toInt(), endStation.toInt(), "distance") ?: error("경로를 찾을 수 없습니다.")
        val (dis_path, dis_line) = disPath
        val (dis_totalCost, dis_totalDis, dis_totalTime) = distance
        val (timePath, time) = dijkstra(SharedData.subwayMap, startStation.toInt(), endStation.toInt(), "time") ?: error("경로를 찾을 수 없습니다.")
        val (time_path, time_line) = timePath
        val (time_totalCost, time_totalDis, time_totalTime) = time

        //경로별 역 갯수 계산
        var cost_count = 0
        for(i in cost_path){
            cost_count++
        }
        var dis_count = 0
        for(i in dis_path){
            dis_count++
        }
        var time_count = 0
        for(i in time_path){
            time_count++
        }

        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm")

        //출발역 도착역 표시
        searchResultTitle.text = "$startStation → $endStation"

        // 경로 데이터 설정 (샘플 데이터를 사용하여 초기화)
        routeDetailsList = mutableListOf(
            RouteDetails(
                title = "최소 비용 경로",
                travelTime = "" + cost_totalTime/60 + "분 " + cost_totalTime%60 + "초",
                startingTime = "" + currentTime.format(formatter) + " 출발",
                cost = "" + cost_totalCost + "원",
                stationCount = "" + (cost_count - 1) + "개 역 이동",
                totalDistance = "총  거리: " + cost_totalDis + "m",
                departureTime = "출발 : " + currentTime.format(formatter),
                travelDuration = "" + cost_totalTime/60 + "분 " + cost_totalTime % 60 + "초",
                arrivalTime = "도착 : " + currentTime.plusSeconds(cost_totalTime.toLong()).format(formatter),
                route = cost_path,
                line = cost_line,
                travelTimeInt = cost_totalTime,
                currentTime = currentTime.toString()

            ),
            RouteDetails(
                title = "최단 거리 경로",
                travelTime = "" + dis_totalTime/60 + "분 " + dis_totalTime%60 + "초",
                startingTime = "" + currentTime.format(formatter) + " 출발",
                cost = "" + dis_totalCost + "원",
                stationCount = "" + (dis_count - 1) + "개 역 이동",
                totalDistance = "총  거리: " + dis_totalDis + "m",
                departureTime = "출발 : " + currentTime.format(formatter),
                travelDuration = "" + dis_totalTime/60 + "분 " + dis_totalTime % 60 + "초",
                arrivalTime = "도착 : " + currentTime.plusSeconds(dis_totalTime.toLong()).format(formatter),
                route = dis_path,
                line = dis_line,
                travelTimeInt = dis_totalTime,
                currentTime = currentTime.toString()

            ),
            RouteDetails(
                title = "최소 시간 경로",
                travelTime = "" + time_totalTime/60 + "분 " + time_totalTime%60 + "초",
                startingTime = "" + currentTime.format(formatter) + " 출발",
                cost = "" + time_totalCost + "원",
                stationCount = "" + (time_count - 1) + "개 역 이동",
                totalDistance = "총  거리: " + time_totalDis + "m",
                departureTime = "출발 : " + currentTime.format(formatter),
                travelDuration = "" + time_totalTime/60 + "분 " + time_totalTime % 60 + "초",
                arrivalTime = "도착 : " + currentTime.plusSeconds(time_totalTime.toLong()).format(formatter),
                route = time_path,
                line = time_line,
                travelTimeInt = time_totalTime,
                currentTime = currentTime.toString()
            )
        )

        // 박스 ID와 데이터 연결
        val boxes = listOf(
            R.id.box1 to routeDetailsList[0],
            R.id.box2 to routeDetailsList[1],
            R.id.box3 to routeDetailsList[2]
        )

        // 박스 클릭 리스너 설정 (클릭 시 상세 정보 페이지로 이동)
        for ((boxId, routeDetails) in boxes) {
            val box: LinearLayout = findViewById(boxId)
            box.setOnClickListener {
                navigateToDetails(routeDetails)
            }
        }
        val currentRoute = Pair(startStation, endStation)

        // SharedPreferences에서 즐겨찾기 로드
        loadFavoriteSearches()

        // 즐겨찾기 상태 확인 및 버튼 업데이트
        updateFavoriteStarButton(currentRoute, favoriteStarButton)

        // 즐겨찾기 버튼 클릭 이벤트 설정
        favoriteStarButton.setOnClickListener {
            toggleFavorite(currentRoute, favoriteStarButton)
        }

        // 박스 UI 업데이트
        updateBoxUI(routeDetailsList)

        // 실시간 UI 업데이트 시작
        startAutoUpdate()
    }

    /**
     * 경로 정보를 UI에 표시합니다.
     */
    private fun updateBoxUI(routeDetailsList: List<RouteDetails>) {
        // 각 박스의 데이터 설정
        for ((index, route) in routeDetailsList.withIndex()) {
            when (index) {
                0 -> {
                    findViewById<TextView>(R.id.title1).text = route.title
                    findViewById<TextView>(R.id.startingTime1).text = route.startingTime
                    findViewById<TextView>(R.id.travelTimeText1).text = route.travelTime
                    findViewById<TextView>(R.id.cost1).text = route.cost
                    findViewById<TextView>(R.id.stationCount1).text = route.stationCount
                    findViewById<TextView>(R.id.totalDistance1).text = route.totalDistance
                    findViewById<TextView>(R.id.arrivalTime1).text = route.arrivalTime
                    findViewById<TextView>(R.id.travelDuration1).text = route.travelDuration
                    findViewById<TextView>(R.id.departureTime1).text = route.departureTime
                }
                1 -> {
                    findViewById<TextView>(R.id.title2).text = route.title
                    findViewById<TextView>(R.id.startingTime2).text = route.startingTime
                    findViewById<TextView>(R.id.travelTimeText2).text = route.travelTime
                    findViewById<TextView>(R.id.cost2).text = route.cost
                    findViewById<TextView>(R.id.stationCount2).text = route.stationCount
                    findViewById<TextView>(R.id.totalDistance2).text = route.totalDistance
                    findViewById<TextView>(R.id.arrivalTime2).text = route.arrivalTime
                    findViewById<TextView>(R.id.travelDuration2).text = route.travelDuration
                    findViewById<TextView>(R.id.departureTime2).text = route.departureTime
                }
                2 -> {
                    findViewById<TextView>(R.id.title3).text = route.title
                    findViewById<TextView>(R.id.startingTime3).text = route.startingTime
                    findViewById<TextView>(R.id.travelTimeText3).text = route.travelTime
                    findViewById<TextView>(R.id.cost3).text = route.cost
                    findViewById<TextView>(R.id.stationCount3).text = route.stationCount
                    findViewById<TextView>(R.id.totalDistance3).text = route.totalDistance
                    findViewById<TextView>(R.id.arrivalTime3).text = route.arrivalTime
                    findViewById<TextView>(R.id.travelDuration3).text = route.travelDuration
                    findViewById<TextView>(R.id.departureTime3).text = route.departureTime
                }
            }
        }
    }
    /**
     * 문자열로 된 시간을 밀리초로 변환합니다.
     * @param timeString 시간 문자열 (예: "오전 9:00")
     * @return 밀리초 단위의 시간
     */
    private fun parseTime(timeString: String): Long {
        val formatter = SimpleDateFormat("a h:mm", Locale.getDefault())
        return try {
            val date = formatter.parse(timeString)
            val calendar = Calendar.getInstance()
            if (date != null) {
                val parsedCalendar = Calendar.getInstance().apply { time = date }
                calendar.set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
            }
            calendar.timeInMillis
        } catch (e: Exception) {
            System.currentTimeMillis() // 파싱 실패 시 현재 시간 반환
        }
    }


    /**
     * 실시간으로 경로 데이터를 업데이트합니다.
     */
    private fun startAutoUpdate() {
        updateHandler = Handler(Looper.getMainLooper())
        updateRunnable = object : Runnable {
            override fun run() {
                // 현재 시간 가져오기
                val currentTime = LocalTime.now()

                // 경로 데이터 업데이트
                routeDetailsList = routeDetailsList.map { route ->
                    // travelTime을 초 단위로 변환
                    val travelTimeParts = route.travelTime.split("분", "초").map { it.trim().toIntOrNull() ?: 0 }
                    val travelTimeInSeconds = travelTimeParts[0] * 60 + travelTimeParts[1]

                    // 출발 시간: 현재 시간
                    val updatedDepartureTime = currentTime

                    // 도착 시간: 출발 시간 + 소요 시간
                    val updatedArrivalTime = updatedDepartureTime.plusSeconds(travelTimeInSeconds.toLong())

                    // RouteDetails 객체 업데이트
                    route.copy(
                        startingTime = "출발: ${updatedDepartureTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))}",
                        departureTime = "출발: ${updatedDepartureTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))}",
                        arrivalTime = "도착: ${updatedArrivalTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))}"
                    )
                }.toMutableList()
                updateBoxUI(routeDetailsList)
                updateHandler.postDelayed(this, 1000)
            }
        }
        updateHandler.post(updateRunnable)
    }

    /**
     * 특정 경로의 상세 정보 페이지로 이동합니다.
     */
    private fun navigateToDetails(routeDetails: RouteDetails) {
        val intent = Intent(this, RouteDetailsResultsActivity::class.java).apply {
            putExtra("routeDetails", routeDetails)
        }
        startActivity(intent)
    }

    /**
     * 즐겨찾기 상태를 버튼 UI에 반영합니다.
     */
    private fun updateFavoriteStarButton(route: Pair<String, String>, button: ImageButton) {
        if (favoriteSearches.contains(route)) {
            button.setImageResource(R.drawable.ic_star_gold) // 채워진 별
        } else {
            button.setImageResource(R.drawable.ic_star_gray) // 빈 별
        }
    }

    /**
     * 즐겨찾기를 추가하거나 삭제합니다.
     */
    private fun toggleFavorite(route: Pair<String, String>, button: ImageButton) {
        if (favoriteSearches.contains(route)) {
            favoriteSearches.remove(route)
            showToast("즐겨찾기에서 삭제되었습니다.")
        } else {
            if (favoriteSearches.size >= 5) {
                showToast("즐겨찾기 목록은 최대 5개입니다.")
                return
            }
            favoriteSearches.add(route)
            showToast("즐겨찾기에 추가되었습니다.")
        }
        saveFavoriteSearches()
        updateFavoriteStarButton(route, button)
    }

    /**
     * SharedPreferences에서 즐겨찾기 데이터를 로드합니다.
     */
    private fun loadFavoriteSearches() {
        favoriteSearches.clear()
        val savedFavorites = sharedPreferences.getStringSet("favorites", emptySet())
        savedFavorites?.forEach { record ->
            val splitRecord = record.split("->")
            if (splitRecord.size == 2) {
                favoriteSearches.add(Pair(splitRecord[0].trim(), splitRecord[1].trim()))
            }
        }
    }

    /**
     * 즐겨찾기 데이터를 SharedPreferences에 저장합니다.
     */
    private fun saveFavoriteSearches() {
        val editor = sharedPreferences.edit()
        val favoritesSet = favoriteSearches.map { "${it.first} -> ${it.second}" }.toSet()
        editor.putStringSet("favorites", favoritesSet)
        editor.apply()
    }

    /**
     * 메시지를 Toast로 표시합니다.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 시간을 "오전/오후 시:분" 형식으로 변환합니다.
     */
    private fun formatTime(timeMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (hourOfDay >= 12) "오후" else "오전"
        val adjustedHour = if (hourOfDay == 0) 12 else if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
        return "$amPm $adjustedHour:${minute.toString().padStart(2, '0')}"
    }

    /**
     * Activity가 파괴될 때 실행 중인 업데이트를 중단합니다.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        if (::updateHandler.isInitialized) {
            updateHandler.removeCallbacks(updateRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::updateHandler.isInitialized) {
            updateHandler.removeCallbacks(updateRunnable)
        }
    }
}
