package com.example.soundmap.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R
import android.content.SharedPreferences

/**
 * `MainActivity` 클래스
 * - 앱의 진입점으로, 로딩 화면 역할을 수행.
 * - 로딩 후 `AppStartActivity`로 이동.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SharedPreferences 초기화
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        // 첫 실행 여부 확인
        if (isFirstRun) {
            // 첫 실행 시 모드 선택 화면으로 이동
            val intent = Intent(this, AppStartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            // 저장된 모드 확인 후 해당 모드로 이동
            val selectedMode = sharedPreferences.getString("selectedMode", "general")
            val intent = if (selectedMode == "special") {
                Intent(this, SpecialModeActivity::class.java)
            } else {
                Intent(this, GeneralModeActivity::class.java)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // 현재 Activity 종료
        finish()
    }
}
