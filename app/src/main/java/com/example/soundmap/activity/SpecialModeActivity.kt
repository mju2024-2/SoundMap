package com.example.soundmap.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import android.media.MediaPlayer
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.soundmap.controller.PathFindController
import com.example.soundmap.controller.VoiceController
import com.example.soundmap.model.TextToSpeechService

class SpecialModeActivity : AppCompatActivity(),
    VoiceController.VoiceInputListener,
    PathFindController.PathFindListener
{
    private lateinit var voiceController: VoiceController
    private lateinit var pathFindController: PathFindController

    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerInitialized = false // MediaPlayer 초기화 여부 확인
    private lateinit var ttsService: TextToSpeechService // TTS 서비스 추가
    private var isDeparture = true
    var departure : String = ""
    var arrival : String = ""
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            voiceController.startListening(isDeparture)
        } else {
            Toast.makeText(this, "음성 인식은 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            initializeMediaPlayer()
        }
    }

    private fun checkPermissionAndStartListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            voiceController.startListening(isDeparture)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_mode)

        voiceController = VoiceController(this, this)
        pathFindController = PathFindController(this, this)
        ttsService = TextToSpeechService(this) // TTS 서비스 초기화

        // MediaPlayer 초기화
        initializeMediaPlayer()

        // 출발지 클릭 이벤트
        findViewById<View>(R.id.departure_area).setOnClickListener {
            val message = "출발지를 말해주세요."
            showToastAndSpeak(message)
            isDeparture = true
            releaseMediaPlayer()
            checkPermissionAndStartListening()

        }

        // 도착지 클릭 이벤트
        findViewById<View>(R.id.arrival_area).setOnClickListener {
            val message = "도착지를 말해주세요."
            showToastAndSpeak(message)
            isDeparture = false
            releaseMediaPlayer()
            checkPermissionAndStartListening()
        }
        // 롱클릭 이벤트 - 출발지
        findViewById<View>(R.id.departure_area).setOnLongClickListener {
            showSearchScreen()
            true // 롱클릭 이벤트 소모 처리
        }

        // 롱클릭 이벤트 - 도착지
        findViewById<View>(R.id.arrival_area).setOnLongClickListener {
            showSearchScreen()
            true
        }
        // 메뉴 버튼 클릭 이벤트
        findViewById<View>(R.id.menu_button).setOnClickListener {
            val intent = Intent(this, SpecialMenuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onVoiceInputReceived(isDeparture: Boolean, result: String) {
        if (isDeparture) {
            findViewById<TextView>(R.id.departure_text).setText(result)
            departure = result
        } else {
            findViewById<TextView>(R.id.arrival_text).setText(result)
            arrival = result
        }

        initializeMediaPlayer()
    }

    override fun displayError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun displayCostResult(result: String) {}

    override fun displayDistanceResult(result: String) {}

    override fun displayTimeResult(result: String) {}

    // MediaPlayer 초기화 메서드
    private fun initializeMediaPlayer() {
        if (mediaPlayer == null) { // MediaPlayer가 초기화되지 않았을 때만 초기화
            mediaPlayer = MediaPlayer.create(this, R.raw.search_announcement).apply {
                isLooping = true // 반복 재생 설정
                start() // 재생 시작
            }
        }
    }

    // MediaPlayer 해제 메서드
    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null // MediaPlayer 객체 해제
    }

    // Activity가 백그라운드로 갈 때 MediaPlayer 해제
    override fun onPause() {
        super.onPause()
        releaseMediaPlayer()
    }

    // Activity가 포그라운드로 돌아올 때 MediaPlayer 다시 초기화
    override fun onResume() {
        super.onResume()
        initializeMediaPlayer()
    }
    // 검색 화면 표시
    private fun showSearchScreen() {
        val intent = Intent(this, SearchActivity::class.java).apply{
            putExtra("startStation",departure )
            putExtra("endStation",arrival )
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceController.destroy()
    }
    // Toast와 TTS를 함께 실행
    private fun showToastAndSpeak(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        ttsService.speak(message)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 상태 저장
        outState.putString("departure", departure)
        outState.putString("arrival", arrival)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 상태 복원
        departure = savedInstanceState.getString("departure", "")
        arrival = savedInstanceState.getString("arrival", "")

        findViewById<TextView>(R.id.departure_text).text = departure
        findViewById<TextView>(R.id.arrival_text).text = arrival
    }
}
