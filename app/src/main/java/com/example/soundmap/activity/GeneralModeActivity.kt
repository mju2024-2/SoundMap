package com.example.soundmap.activity

import android.Manifest
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.example.soundmap.R
import com.example.soundmap.com.example.soundmap.SubwayMapScreen

class GeneralModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View 초기화
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        val voiceButton: ImageButton = findViewById(R.id.voiceButton)
        val settingsButton: ImageButton = findViewById(R.id.settingsButton)
        val composeSubwayMap: ComposeView = findViewById(R.id.composeSubwayMap)

        // 메뉴 버튼 클릭 이벤트
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // 음성인식 버튼 클릭 이벤트
        voiceButton.setOnClickListener {
            Toast.makeText(this, "음성인식 버튼 클릭됨!", Toast.LENGTH_SHORT).show()
        }

        // 설정 버튼 클릭 이벤트
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // 검색창 클릭 이벤트
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.setOnClickListener {
            val intent = Intent(this, SearchResultsActivity::class.java)
            startActivity(intent)
        }

        // ComposeView에 SubwayMapScreen 렌더링
        composeSubwayMap.setContent {
            SubwayMapScreen(onBackPressed = { onBackPressed() })
        }
    }
}
