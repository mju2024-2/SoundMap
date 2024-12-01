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

        // 경로 데이터 설정 (샘플 데이터를 사용하여 초기화)
        routeDetailsList = mutableListOf(
            RouteDetails(
                title = "최단 경로",
                travelTime = "22분",
                startingTime = formatTime(System.currentTimeMillis()) + " 출발",
                cost = "1500원",
                routeType = "수인분당선",
                routeDetails = "수원역 / 고색역 / 망포역",
                stationCount = "8개 역",
                totalDistance = "총 거리: 10.8km",
                departureTime = formatTime(System.currentTimeMillis()),
                travelDuration = "22분",
                arrivalTime = formatTime(System.currentTimeMillis() + 22 * 60 * 1000)
            ),
            RouteDetails(
                title = "최소 비용",
                travelTime = "25분",
                startingTime = formatTime(System.currentTimeMillis()) + " 출발",
                cost = "1400원",
                routeType = "분당선",
                routeDetails = "수원역 / 매탄권선역",
                stationCount = "5개 역",
                totalDistance = "총 거리: 10.8km",
                departureTime = formatTime(System.currentTimeMillis()),
                travelDuration = "25분",
                arrivalTime = formatTime(System.currentTimeMillis() + 25 * 60 * 1000)
            ),
            RouteDetails(
                title = "최소 거리",
                travelTime = "30분",
                startingTime = formatTime(System.currentTimeMillis()) + " 출발",
                cost = "1600원",
                routeType = "1호선",
                routeDetails = "수원역 / 성균관대역",
                stationCount = "7개 역",
                totalDistance = "총 거리: 10.8km",
                departureTime = formatTime(System.currentTimeMillis()),
                travelDuration = "30분",
                arrivalTime = formatTime(System.currentTimeMillis() + 30 * 60 * 1000)
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

        // Intent로 전달받은 출발역과 도착역 정보 가져오기
        val startStation = intent.getStringExtra("startStation") ?: "출발역"
        val endStation = intent.getStringExtra("endStation") ?: "도착역"
        searchResultTitle.text = "$startStation → $endStation"

        // SharedPreferences에서 즐겨찾기 로드
        loadFavoriteSearches()

        // 즐겨찾기 상태 확인 및 버튼 업데이트
        val currentRoute = Pair(startStation, endStation)
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
            // `arrivalTime`과 `travelDuration`을 계산
            val travelDurationMinutes = route.travelDuration.replace("분", "").toIntOrNull() ?: 0
            val updatedArrivalTimeMillis = parseTime(route.departureTime) + travelDurationMinutes * 60 * 1000

            when (index) {
                0 -> {
                    findViewById<TextView>(R.id.startingTime1).text = route.startingTime
                    findViewById<TextView>(R.id.travelTimeText1).text = route.travelTime
                    findViewById<TextView>(R.id.cost1).text = route.cost
                    findViewById<TextView>(R.id.routeType1).text = route.routeType
                    findViewById<TextView>(R.id.routeDetails1).text = route.routeDetails
                    findViewById<TextView>(R.id.arrivalTime1).text = formatTime(updatedArrivalTimeMillis)
                    findViewById<TextView>(R.id.travelDuration1).text = route.travelDuration
                    findViewById<TextView>(R.id.departureTime1).text = route.departureTime
                }
                1 -> {
                    findViewById<TextView>(R.id.startingTime2).text = route.startingTime
                    findViewById<TextView>(R.id.travelTimeText2).text = route.travelTime
                    findViewById<TextView>(R.id.cost2).text = route.cost
                    findViewById<TextView>(R.id.routeType2).text = route.routeType
                    findViewById<TextView>(R.id.routeDetails2).text = route.routeDetails
                    findViewById<TextView>(R.id.arrivalTime2).text = formatTime(updatedArrivalTimeMillis)
                    findViewById<TextView>(R.id.travelDuration2).text = route.travelDuration
                    findViewById<TextView>(R.id.departureTime2).text = route.departureTime
                }
                2 -> {
                    findViewById<TextView>(R.id.startingTime3).text = route.startingTime
                    findViewById<TextView>(R.id.travelTimeText3).text = route.travelTime
                    findViewById<TextView>(R.id.cost3).text = route.cost
                    findViewById<TextView>(R.id.routeType3).text = route.routeType
                    findViewById<TextView>(R.id.routeDetails3).text = route.routeDetails
                    findViewById<TextView>(R.id.arrivalTime3).text = formatTime(updatedArrivalTimeMillis)
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
                val currentTimeMillis = System.currentTimeMillis()
                routeDetailsList = routeDetailsList.map { route ->
                    val travelMinutes = route.travelTime.replace("분", "").toIntOrNull() ?: 0
                    val updatedDepartureTimeMillis = currentTimeMillis
                    val updatedArrivalTimeMillis = updatedDepartureTimeMillis + travelMinutes * 60 * 1000
                    route.copy(
                        startingTime = formatTime(System.currentTimeMillis()) + " 출발",
                        departureTime = formatTime(updatedDepartureTimeMillis),
                        arrivalTime = formatTime(updatedArrivalTimeMillis)
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
    override fun onDestroy() {
        super.onDestroy()
        updateHandler.removeCallbacks(updateRunnable)
    }
}
