package com.example.soundmap.activity

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.soundmap.R

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 3초 후 액티비티 종료
        Handler().postDelayed({
            finish()
        }, 3000) // 3000ms = 3초
    }
}
