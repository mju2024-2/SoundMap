package com.example.soundmap.activity

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import com.example.soundmap.model.RouteDetails
import com.example.soundmap.model.RouteSegment
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

/**
 * `RouteDetailsResultsActivity` 클래스
 * - 특정 경로의 상세 정보를 표시하는 Activity.
 * - 경로 정보를 세부적으로 보여주는 UI를 생성 및 업데이트합니다.
 */
class RouteDetailsResultsActivity : AppCompatActivity() {

    // 경로 세부 정보를 담는 컨테이너 레이아웃
    private lateinit var routeDetailsContainer: LinearLayout

    // 실시간 UI 업데이트를 위한 핸들러와 실행 가능한 객체
    private lateinit var updateHandler: Handler
    private lateinit var updateRunnable: Runnable

    // 전달받은 경로 상세 정보 객체
    private var routeDetails: RouteDetails? = null

    /**
     * Activity 생성 시 호출되는 메서드.
     * - 초기 UI 설정 및 데이터를 기반으로 화면을 구성합니다.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details_results)

        // 레이아웃 컨테이너 연결
        routeDetailsContainer = findViewById(R.id.routeDetailsContainer)

        // Intent에서 `RouteDetails` 객체를 가져옴
        routeDetails = intent.getParcelableExtra("routeDetails")

        // 전달받은 데이터를 기반으로 UI 업데이트 및 실시간 업데이트 시작
        if (routeDetails != null) {
            updateUI()
            startAutoUpdate()
            populateRouteDetails(getRouteSegments()) // 샘플 데이터로 노선 세부 정보 추가
        } else {
            val routeSegments = getRouteSegments()
            populateRouteDetails(routeSegments)
        }
    }

    /**
     * 샘플 데이터 생성 메서드.
     * - 실제 경로 알고리즘과 연결되기 전 테스트용 데이터를 반환.
     */
    private fun getRouteSegments(): List<RouteSegment> {
        return listOf(
            RouteSegment("1호선", 5, listOf("역1", "역2", "역3", "역4", "역5"), "#FF0000"),
            RouteSegment("2호선", 3, listOf("역6", "역7", "역8"), "#0000FF")
        )
    }

    /**
     * 전달받은 노선 데이터를 UI로 변환하여 표시합니다.
     * - 노선별로 역과 선의 구성을 동적으로 생성.
     */
    private fun populateRouteDetails(routeSegments: List<RouteSegment>) {
        for (segment in routeSegments) {
            // 노선 제목 추가
            val lineTitle = TextView(this).apply {
                text = getString(R.string.line_title, segment.lineType, segment.stationCount)
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(0, 16, 0, 50) // 제목과 노선 간 간격 추가
            }
            routeDetailsContainer.addView(lineTitle)

            // 노선 전체 레이아웃 (세로)
            val routeLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
            }

            for ((index, station) in segment.stations.withIndex()) {
                // 역 정보 컨테이너 (수평)
                val stationRow = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL // 자식 요소 수직 중앙 정렬
                }

                // 세로선과 동그라미를 포함한 컨테이너
                val lineAndCircleContainer = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        50, LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                // 동그라미 아이콘
                val stationIcon = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    background = ContextCompat.getDrawable(this@RouteDetailsResultsActivity, R.drawable.circle_icon)
                }

                // 세로선 위쪽 (첫 번째 역 제외)
                if (index > 0) {
                    val lineAbove = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(8, 50).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        setBackgroundColor(Color.parseColor(segment.lineColor))
                    }
                    lineAndCircleContainer.addView(lineAbove)
                } else {
                    // 첫 번째 역의 위쪽 빈 공간
                    lineAndCircleContainer.addView(View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(8, 25)
                    })
                }

                // 동그라미 추가
                lineAndCircleContainer.addView(stationIcon)

                // 세로선 아래쪽 (마지막 역 제외)
                if (index < segment.stations.size - 1) {
                    val lineBelow = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(8, 50).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        setBackgroundColor(Color.parseColor(segment.lineColor))
                    }
                    lineAndCircleContainer.addView(lineBelow)
                } else {
                    // 마지막 역의 아래쪽 빈 공간
                    lineAndCircleContainer.addView(View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(8, 25)
                    })
                }

                // 역 이름
                val stationName = TextView(this).apply {
                    text = station
                    textSize = 14f
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    gravity = Gravity.CENTER_VERTICAL // 수직 중앙 정렬
                    setPadding(16, 0, 0, 0) // 동그라미와 이름 간 간격
                }

                // 수평으로 세로선+동그라미와 역 이름 배치
                stationRow.addView(lineAndCircleContainer)
                stationRow.addView(stationName)

                // 노선 레이아웃에 추가
                routeLayout.addView(stationRow)
            }

            // 노선 레이아웃을 메인 컨테이너에 추가
            routeDetailsContainer.addView(routeLayout)
        }
    }

    /**
     * UI를 업데이트하는 메서드.
     * - 경로 정보를 텍스트로 표시.
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val travelMinutes = routeDetails?.travelTime?.replace("분", "")?.toIntOrNull() ?: 0
        val departureTimeMillis = routeDetails?.departureTime?.let { parseTime(it) } ?: System.currentTimeMillis()
        val arrivalTimeMillis = departureTimeMillis + (travelMinutes * 60 * 1000)

        // UI 요소 업데이트
        findViewById<TextView>(R.id.routeTitle).text = routeDetails?.title ?: "경로 정보"
        findViewById<TextView>(R.id.travelTime).text = routeDetails?.travelTime ?: "N/A"
        findViewById<TextView>(R.id.startingTime).text = "출발: ${formatTime(System.currentTimeMillis())}"
        findViewById<TextView>(R.id.departureTime).text = "${formatTime(departureTimeMillis)}"
        findViewById<TextView>(R.id.arrivalTime).text = "${formatTime(arrivalTimeMillis)}"
        findViewById<TextView>(R.id.cost).text = routeDetails?.cost ?: "N/A"
        findViewById<TextView>(R.id.routeType).text = routeDetails?.routeType ?: "N/A"
        findViewById<TextView>(R.id.routeDetails).text = routeDetails?.routeDetails ?: "N/A"
        findViewById<TextView>(R.id.stationCount).text = routeDetails?.stationCount ?: "N/A"
        findViewById<TextView>(R.id.totalDistance).text = routeDetails?.totalDistance ?: "N/A"
        findViewById<TextView>(R.id.travelDuration).text = routeDetails?.travelDuration ?: "N/A"
    }

    /**
     * 실시간 UI 업데이트를 시작하는 메서드.
     */
    private fun startAutoUpdate() {
        updateHandler = Handler(Looper.getMainLooper())
        updateRunnable = object : Runnable {
            override fun run() {
                updateUI()
                updateHandler.postDelayed(this, 1000)
            }
        }
        updateHandler.post(updateRunnable)
    }

    /**
     * Activity가 파괴될 때 실행 중인 업데이트를 중단.
     */
    override fun onDestroy() {
        super.onDestroy()
        updateHandler.removeCallbacks(updateRunnable)
    }

    /**
     * 시간을 포맷팅하여 반환.
     */
    private fun formatTime(timeMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (hour >= 12) "오후" else "오전"
        val adjustedHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        return "$amPm $adjustedHour:${minute.toString().padStart(2, '0')}"
    }

    /**
     * 문자열 시간 데이터를 밀리초로 변환.
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
            System.currentTimeMillis()
        }
    }
}
