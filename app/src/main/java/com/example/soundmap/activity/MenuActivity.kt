package com.example.soundmap.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import android.content.SharedPreferences
import android.view.View
import android.content.Intent
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        // SharedPreferences 초기화
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // 모드 전환 클릭 이벤트
        val modeSwitchLabel: TextView = findViewById(R.id.modeSwitchLabel)
        modeSwitchLabel.setOnClickListener {
            // 모드 전환 로직 처리
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstRun", true) // 첫 실행 플래그 설정
            editor.apply()

            // 모드 선택 화면으로 이동
            val intent = Intent(this, ModeSelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }


    }
}
