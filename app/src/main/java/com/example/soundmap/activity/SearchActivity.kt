package com.example.soundmap.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import com.example.soundmap.model.SharedData
import com.example.soundmap.model.dijkstra
import com.example.soundmap.model.TextToSpeechService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var ttsService: TextToSpeechService // TextToSpeechService 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // TextToSpeechService 초기화
        ttsService = TextToSpeechService(this)

        // 랜덤으로 재생할 음원 파일 리스트
        val soundResIds = listOf(
            R.raw.subway1,
            R.raw.subway2,
            R.raw.subway3
        )

        // 랜덤으로 하나 선택
        val randomSoundResId = soundResIds.random()

        // MediaPlayer 초기화 및 재생
        mediaPlayer = MediaPlayer.create(this, randomSoundResId)
        mediaPlayer?.setOnCompletionListener {
            val startStation = intent.getStringExtra("startStation") ?: ""
            val endStation = intent.getStringExtra("endStation") ?: ""

            // 입력값 검증
            if (startStation.isEmpty() || endStation.isEmpty()) {
                showToastAndSpeak("출발역과 도착역을 모두 입력해주세요.")
                returnToPreviousScreen()
                return@setOnCompletionListener
            }

            val startStationInt = startStation.toIntOrNull()
            val endStationInt = endStation.toIntOrNull()

            if (startStationInt == null || endStationInt == null) {
                showToastAndSpeak("출발역과 도착역은 숫자로 입력해주세요.")
                returnToPreviousScreen()
                return@setOnCompletionListener
            }

            val result = dijkstra(SharedData.subwayMap, startStationInt, endStationInt, "distance")

            if (result == null) {
                showToastAndSpeak("검색 결과를 찾을 수 없습니다.")
                returnToPreviousScreen()
            } else {
                moveToResultActivity(startStation, endStation) // 검색 성공 시 결과 화면으로 이동
            }
        }
        mediaPlayer?.start()
    }

    private fun moveToResultActivity(startStation: String, endStation: String) {
        val successIntent = Intent(this, SpecialSearchResultActivity::class.java).apply {
            putExtra("startStation", startStation) // 음성 인식 값 사용
            putExtra("endStation", endStation)
        }
        startActivity(successIntent)
        finish()
    }

    private fun returnToPreviousScreen() {
        // 코루틴을 사용하여 딜레이 추가
        lifecycleScope.launch {
            delay(3000) // 3초 딜레이
            val failIntent = Intent(this@SearchActivity, SpecialModeActivity::class.java) // 명시적으로 Activity를 지정
            startActivity(failIntent)
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // MediaPlayer 해제
        mediaPlayer?.release()
        mediaPlayer = null

        // TTS 서비스 종료
        ttsService.shutdown()
    }

    private fun showToastAndSpeak(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        ttsService.speak(message) // TextToSpeechService를 통해 TTS로 메시지 읽기
    }

}