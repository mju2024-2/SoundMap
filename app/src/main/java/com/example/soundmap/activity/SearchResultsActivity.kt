package com.example.soundmap.activity
import android.Manifest
import android.text.InputFilter
import android.content.Intent
import android.widget.ImageView
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.soundmap.R
import com.example.soundmap.controller.RecentSearchController
import com.example.soundmap.controller.VoiceController
import com.example.soundmap.controller.PathFindController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SearchResultsActivity : AppCompatActivity(),
    VoiceController.VoiceInputListener,
    PathFindController.PathFindListener
{
    private lateinit var recentSearchController: RecentSearchController
    private lateinit var voiceController: VoiceController
    private lateinit var pathFindController: PathFindController

    private val sharedPreferences by lazy {
        getSharedPreferences("recentSearches", Context.MODE_PRIVATE)
    }
    private val recentSearches = mutableListOf<Triple<String, String, String>>()
    private val favoriteSearches = mutableListOf<Pair<String, String>>() // 즐겨찾기 데이터 추가

    private var isDeparture = true

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            voiceController.startListening(isDeparture)
        } else {
            Toast.makeText(this, "음성 인식은 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
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
        setContentView(R.layout.activity_search_results)

        recentSearchController = RecentSearchController(this)
        voiceController = VoiceController(this, this)
        pathFindController = PathFindController(this, this)

        val recentSearchesContainer: LinearLayout = findViewById(R.id.recentSearchesContainer)
        val favoriteSearchesContainer: LinearLayout = findViewById(R.id.favoriteSearchesContainer)

        val startVoiceButton: ImageButton = findViewById(R.id.startVoiceButton)
        val endVoiceButton: ImageButton = findViewById(R.id.endVoiceButton)

        val startEditText: EditText = findViewById(R.id.startEditText)
        val endEditText: EditText = findViewById(R.id.endEditText)

        startEditText.filters = arrayOf(InputFilter.LengthFilter(10))
        endEditText.filters = arrayOf(InputFilter.LengthFilter(10))

        startVoiceButton.setOnClickListener {
            isDeparture = true
            checkPermissionAndStartListening()
        }

        endVoiceButton.setOnClickListener {
            isDeparture = false
            checkPermissionAndStartListening()
        }

        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {

            val startStation = startEditText.text.toString().trim()
            val endStation = endEditText.text.toString().trim()
            pathFindController.onSearchButtonClicked(startStation, endStation)

            if (startStation.isEmpty() || endStation.isEmpty()) {
                Toast.makeText(this, "출발역과 도착역을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                addRecentSearch(startStation, endStation)

                val currentTimeMillis = System.currentTimeMillis()
                val intent = Intent(this, RouteResultsActivity::class.java).apply {
                    putExtra("startStation", startStation)
                    putExtra("endStation", endStation)
                    putExtra("searchTime", currentTimeMillis)
                }
                startActivity(intent)
            }
        }

        loadRecentSearches()
        loadFavoriteSearches()

        updateRecentSearchesUI(recentSearchesContainer)
        updateFavoriteSearchesUI(favoriteSearchesContainer)
    }

    override fun onVoiceInputReceived(isDeparture: Boolean, result: String) {
        if (isDeparture) {
            findViewById<EditText>(R.id.startEditText).setText(result)
        } else {
            findViewById<EditText>(R.id.endEditText).setText(result)
        }
    }

    override fun displayError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun displayCostResult(result: String) {}

    override fun displayDistanceResult(result: String) {}

    override fun displayTimeResult(result: String) {}

    private fun addRecentSearch(start: String, end: String) {
        val timestamp = getCurrentTimestamp()
        recentSearches.add(Triple(start, end, timestamp)) // 최신 기록 추가
        if (recentSearches.size > 4) {
            recentSearches.removeAt(0) // 오래된 기록 제거
        }
        saveRecentSearches() // 저장
        updateRecentSearchesUI(findViewById(R.id.recentSearchesContainer)) // UI 업데이트
    }


    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun loadRecentSearches() {
        recentSearches.clear()
        val savedSearches = sharedPreferences.getString("orderedSearches", null)
        savedSearches?.split("|")?.forEach { record ->
            val parts = record.split("->")
            if (parts.size == 3) {
                val start = parts[0].trim()
                val end = parts[1].trim()
                val timestamp = parts[2].trim()
                recentSearches.add(Triple(start, end, timestamp))
            }
        }
    }


    private fun saveRecentSearches() {
        val editor = sharedPreferences.edit()
        val searchStrings = recentSearches.map { "${it.first} -> ${it.second} -> ${it.third}" }
        editor.putString("orderedSearches", searchStrings.joinToString("|")) // JSON 형식으로 저장
        editor.apply()
    }


    private fun loadFavoriteSearches() {
        favoriteSearches.clear()
        val savedFavorites = sharedPreferences.getStringSet("favorites", emptySet())
        savedFavorites?.forEach { record ->
            val splitRecord = record.split("->")
            if (splitRecord.size == 2) {
                val start = splitRecord[0].trim()
                val end = splitRecord[1].trim()
                favoriteSearches.add(Pair(start, end))
            }
        }
    }

    private fun saveFavoriteSearches() {
        val editor = sharedPreferences.edit()
        val favoriteStrings = favoriteSearches.map { "${it.first} -> ${it.second}" }.toSet()
        editor.putStringSet("favorites", favoriteStrings)
        editor.apply()
    }

    private fun toggleFavorite(record: Pair<String, String>) {
        if (favoriteSearches.contains(record)) {
            // 즐겨찾기에서 삭제
            favoriteSearches.remove(record)
        } else {
            if (favoriteSearches.size < 5) { // 즐겨찾기 최대 6개 제한
                favoriteSearches.add(record)
            } else {
                Toast.makeText(this, "즐겨찾기 목록은 최대 5개 입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        saveFavoriteSearches()

        // 최근 검색 목록과 즐겨찾기 목록 UI 모두 업데이트
        updateRecentSearchesUI(findViewById(R.id.recentSearchesContainer))
        updateFavoriteSearchesUI(findViewById(R.id.favoriteSearchesContainer))
    }



    private fun updateRecentSearchesUI(container: LinearLayout) {
        container.removeAllViews()

        val reversedSearches = recentSearches.reversed()

        for ((index, record) in reversedSearches.withIndex()) {
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(8, 8, 8, 8)
            }

            val searchText = TextView(this).apply {
                text = "${record.first} → ${record.second}"
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
            }

            val timestampText = TextView(this).apply {
                text = record.third
                textSize = 12f
                setTextColor(android.graphics.Color.GRAY)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 100 // 별과 겹치지 않도록 여백 추가
                }
            }

            val favoriteButton = ImageButton(this).apply {
                // 즐겨찾기 여부에 따라 별 이미지 설정
                setImageResource(
                    if (favoriteSearches.contains(Pair(record.first, record.second)))
                        R.drawable.ic_star_gold // 채워진 별
                    else
                        R.drawable.ic_star_gray // 빈 별
                )
                layoutParams = LinearLayout.LayoutParams(60, 60).apply {
                    marginEnd = 30
                }
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                scaleType = ImageView.ScaleType.FIT_XY
                setPadding(0, 0, 0, 0)

                setOnClickListener {
                    toggleFavorite(Pair(record.first, record.second))
                    updateRecentSearchesUI(container) // UI 업데이트
                }
            }

            val deleteButton = ImageButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                    marginEnd = 8
                }
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener {
                    removeSearch(index, container)
                }
            }

            // 검색 기록 항목 클릭 시 RouteResultsActivity로 이동
            itemLayout.setOnClickListener {
                val intent = Intent(this@SearchResultsActivity, RouteResultsActivity::class.java).apply {
                    putExtra("startStation", record.first)
                    putExtra("endStation", record.second)
                }
                startActivity(intent)
            }

            itemLayout.addView(searchText)
            itemLayout.addView(timestampText)
            itemLayout.addView(favoriteButton)
            itemLayout.addView(deleteButton)
            container.addView(itemLayout)
        }
    }


    private fun updateFavoriteSearchesUI(container: LinearLayout) {
        container.removeAllViews()

        for ((index, favorite) in favoriteSearches.withIndex()) {
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(8, 8, 8, 8)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val starButton = ImageButton(this).apply {
                setImageResource(R.drawable.ic_star_gold)
                layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                    marginEnd = 16
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener {
                    removeFavorite(index, container)
                }
            }

            val textBackground = LinearLayout(this).apply {
                background = getDrawable(R.drawable.rounded_background)
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                setPadding(16, 16, 16, 16)
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setOnClickListener {
                    val intent = Intent(this@SearchResultsActivity, RouteResultsActivity::class.java).apply {
                        putExtra("startStation", favorite.first)
                        putExtra("endStation", favorite.second)
                    }
                    startActivity(intent)
                }
            }

            val favoriteText = TextView(this).apply {
                text = "${favorite.first} → ${favorite.second}"
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            textBackground.addView(favoriteText)
            itemLayout.addView(starButton)
            itemLayout.addView(textBackground)
            container.addView(itemLayout)
        }
    }

    private fun removeSearch(index: Int, container: LinearLayout) {
        // reversed()로 인해 UI에서 보여지는 항목의 인덱스를 원래 리스트의 인덱스로 변환
        val originalIndex = recentSearches.size - 1 - index

        // 올바른 인덱스를 사용하여 삭제
        if (originalIndex in recentSearches.indices) {
            recentSearches.removeAt(originalIndex)
            saveRecentSearches()
            updateRecentSearchesUI(container)
        }
    }

    private fun removeFavorite(index: Int, container: LinearLayout) {
        if (index in favoriteSearches.indices) {
            favoriteSearches.removeAt(index)
            saveFavoriteSearches()
            updateFavoriteSearchesUI(container)
        }
    }

    override fun onResume() {
        super.onResume()
        val favoriteSearchesContainer: LinearLayout = findViewById(R.id.favoriteSearchesContainer)

        loadFavoriteSearches()
        updateFavoriteSearchesUI(favoriteSearchesContainer)

        val recentSearchesContainer: LinearLayout = findViewById(R.id.recentSearchesContainer)
        updateRecentSearchesUI(recentSearchesContainer)
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceController.destroy()
    }
}
