package com.example.soundmap.activity

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import com.example.soundmap.model.RouteDetails
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.time.LocalTime
import android.content.Context
import android.os.Build
import android.app.NotificationChannel
import android.app.NotificationManager
import java.time.temporal.ChronoUnit
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.util.Log


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
    private lateinit var routeDetails: RouteDetails

    /**
     * Activity 생성 시 호출되는 메서드.
     * - 초기 UI 설정 및 데이터를 기반으로 화면을 구성합니다.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details_results)
        // 권한 요청
        requestNotificationPermission()

        // 레이아웃 컨테이너 연결
        routeDetailsContainer = findViewById(R.id.routeDetailsContainer)

        // Intent에서 `RouteDetails` 객체를 가져옴
        intent.getParcelableExtra<RouteDetails>("routeDetails")?.let{
            routeDetails = it
        }
        updateUI()

        // 전달받은 데이터를 기반으로 UI 업데이트 및 실시간 업데이트 시작
        if (routeDetails != null) {
            updateUI()


            populateRouteDetails(routeDetails.route, routeDetails.line)
        } else {
            populateRouteDetails(routeDetails.route, routeDetails.line)
        }
        val alarmButton: ImageButton = findViewById(R.id.alarmButton)

        alarmButton.setOnClickListener {
            routeDetails?.let { details ->
                if (details.travelTimeInt >= 300) {
                    showToast("알림 설정이 등록되었습니다. 도착 5분 전에 알림이 울립니다.")
                    alarmButton.setImageResource(R.drawable.alarm_on)

                    val timeString = details.currentTime
                    val currentTime = LocalTime.parse(timeString) // 현재 시간
                    val alarmTime = currentTime.plusSeconds((details.travelTimeInt - 300).toLong()) // currentDateTime에서 5초 뒤로 설정

                    scheduleNotification(currentTime, alarmTime)
                } else {
                    showToast("소요시간이 5분을 넘지 않아 알림 설정을 등록 할 수 없습니다.")
                }
            }
        }
    }
    private fun scheduleNotification(currentTime: LocalTime, alarmTime: LocalTime) {
        // 현재 시간에서 알람 시간까지의 차이 계산
        val delay = ChronoUnit.MILLIS.between(currentTime, alarmTime)

        if (delay <= 0) {
            showToast("알람 시간이 잘못되었습니다.")
            return
        }

        // 로그로 딜레이 확인
        println("알람 딜레이: $delay ms")

        // OneTimeWorkRequest 생성
        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        // WorkManager에 작업 추가
        WorkManager.getInstance(this).enqueue(workRequest)
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
                background = ContextCompat.getDrawable(this@RouteDetailsResultsActivity, R.drawable.circle_icon)
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
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * UI를 업데이트하는 메서드.
     * - 경로 정보를 텍스트로 표시.
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        // 여행 시간을 분과 초로 변환
        val travelMinutes = routeDetails?.travelTime?.split("분")?.get(0)?.trim()?.toIntOrNull() ?: 0
        val travelSeconds = routeDetails?.travelTime?.split("분")?.getOrNull(1)?.replace("초", "")?.trim()?.toIntOrNull() ?: 0
        val travelTimeInMillis = (travelMinutes * 60 + travelSeconds) * 1000L

        // 출발 시간을 밀리초로 변환
        val departureTimeMillis = routeDetails?.departureTime?.let { parseTime(it) } ?: System.currentTimeMillis()

        // 도착 시간 계산
        val arrivalTimeMillis = departureTimeMillis + travelTimeInMillis

        // UI 요소 업데이트
        findViewById<TextView>(R.id.routeTitle).text = routeDetails?.title ?: "경로 정보"
        findViewById<TextView>(R.id.travelTime).text = routeDetails?.travelTime ?: "N/A"
        findViewById<TextView>(R.id.startingTime).text = "출발: ${formatTime(departureTimeMillis)}"
        findViewById<TextView>(R.id.departureTime).text = "출발: ${formatTime(departureTimeMillis)}"
        findViewById<TextView>(R.id.arrivalTime).text = "도착: ${formatTime(arrivalTimeMillis)}"
        findViewById<TextView>(R.id.cost).text = routeDetails?.cost ?: "N/A"
        findViewById<TextView>(R.id.stationCount).text = routeDetails?.stationCount ?: "N/A"
        findViewById<TextView>(R.id.totalDistance).text = routeDetails?.totalDistance ?: "N/A"
        findViewById<TextView>(R.id.travelDuration).text = routeDetails?.travelDuration ?: "N/A"
    }

    /**
     * 실시간 UI 업데이트를 시작하는 메서드.
     */
    // private fun startAutoUpdate() {
    //    updateHandler = Handler(Looper.getMainLooper())
    //   updateRunnable = object : Runnable {
    //      override fun run() {
    //         updateUI()
    //        updateHandler.postDelayed(this, 1000)
    //   }
    // }
    // updateHandler.post(updateRunnable)
    // }

    /**
     * Activity가 파괴될 때 실행 중인 업데이트를 중단.
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
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

}
class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // 알림을 보내기 위한 코드 작성
        val context = applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 생성 (Android 8.0 이상에서 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "도착 알림 채널"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("도착 알림")
            .setContentText("목적지 도착 5분전입니다.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // 알림 표시
        notificationManager.notify(1, builder.build())

        // 작업이 성공적으로 완료되었음을 알림
        return Result.success()
    }

}