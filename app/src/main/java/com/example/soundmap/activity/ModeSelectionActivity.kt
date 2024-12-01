package com.example.soundmap.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import android.content.SharedPreferences

class ModeSelectionActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selection)

        // MediaPlayer 초기화
        initializeMediaPlayer()
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // 일반 모드 클릭 이벤트
        val generalModeArea: TextView = findViewById(R.id.general_mode_area)
        generalModeArea.setOnClickListener {
            saveMode(sharedPreferences, "general") // 일반 모드 저장
            releaseMediaPlayer() // MediaPlayer 해제
            val intent = Intent(this, GeneralModeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // 특수 모드 클릭 이벤트
        val specialModeArea: TextView = findViewById(R.id.special_mode_area)
        specialModeArea.setOnClickListener {
            saveMode(sharedPreferences, "special") // 특수 모드 저장
            releaseMediaPlayer() // MediaPlayer 해제
            val intent = Intent(this, SpecialModeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    // MediaPlayer 초기화 메서드
    private fun initializeMediaPlayer() {
        if (mediaPlayer == null) { // MediaPlayer가 초기화되지 않았을 때만 초기화
            mediaPlayer = MediaPlayer.create(this, R.raw.mode_select).apply {
                isLooping = true // 반복 재생 설정
                start()
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
    private fun saveMode(sharedPreferences: SharedPreferences, mode: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstRun", false) // 첫 실행 플래그 업데이트
        editor.putString("selectedMode", mode) // 선택된 모드 저장
        editor.apply()
    }

}
