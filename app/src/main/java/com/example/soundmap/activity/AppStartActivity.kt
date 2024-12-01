package com.example.soundmap.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R

/**
 * `AppStartActivity` 클래스
 * - 앱의 로딩 화면 역할을 수행.
 * - 3초 대기 후 `ModeSelectionActivity`로 이동.
 */
class AppStartActivity : AppCompatActivity() {

    private val LOADING_TIME: Long = 3000 // 로딩 시간 (밀리초 단위)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_start)

        // 3초 후 `ModeSelectionActivity`로 이동
        Handler().postDelayed({
            val intent = Intent(this, ModeSelectionActivity::class.java)
            startActivity(intent)
            finish() // 로딩 화면 종료
        }, LOADING_TIME)
    }
}
