package com.example.soundmap.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.soundmap.com.example.soundmap.SubwayMapScreen

class SubwayMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubwayMapScreen(
                onBackPressed = { finish() } // 뒤로 가기 처리
            )
        }
    }
}
