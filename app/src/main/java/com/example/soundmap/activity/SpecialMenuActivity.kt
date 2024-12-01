package com.example.soundmap.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import android.content.SharedPreferences

class SpecialMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_menu)
        // SharedPreferences 초기화
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // 모드 전환 버튼 클릭
        findViewById<View>(R.id.mode_switch_button).setOnClickListener {
            // 모드 전환 로직 처리
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstRun", true) // 첫 실행 플래그 설정
            editor.apply()

            val intent = Intent(this, ModeSelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // 지하철 노선도 버튼 클릭
        findViewById<View>(R.id.subway_map_button).setOnClickListener {
            val intent = Intent(this, SubwayMapActivity::class.java)
            startActivity(intent)
        }


    }
}
