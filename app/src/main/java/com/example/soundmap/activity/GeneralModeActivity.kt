package com.example.soundmap.activity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import android.content.Intent
import android.util.Log
import com.example.soundmap.R
import com.example.soundmap.network.WeatherApiClient
import com.example.soundmap.network.WeatherApiService
import com.example.soundmap.model.WeatherResponse
import com.example.soundmap.com.example.soundmap.SubwayMapScreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class GeneralModeActivity : AppCompatActivity() {

    private val apiKey = "9b7acdea39040a6be33394f51535a7cb" // 환경 변수로 분리 필요
    private val cityName = "Yongin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_mode)

        // View 초기화
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        val composeSubwayMap: ComposeView = findViewById(R.id.composeSubwayMap)

        // 메뉴 버튼 클릭 이벤트
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
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

        // 날씨 정보 가져오기
        fetchWeather { weather, description, temp, humidity ->
            val weatherCharacter = findViewById<ImageView>(R.id.weatherCharacter)
            val weatherInfo = findViewById<TextView>(R.id.weatherInfo)
            val temperatureInfo = findViewById<TextView>(R.id.temperatureInfo)
            val humidityInfo = findViewById<TextView>(R.id.humidityInfo)

            weatherCharacter.setImageResource(getWeatherCharacter(weather))
            weatherInfo.text = "오늘의 날씨: $description"
            temperatureInfo.text = "기온: $temp°C"
            humidityInfo.text = "습도: $humidity%"
        }
    }

    private fun fetchWeather(onResult: (String, String, Float, Int) -> Unit) {
        val apiService = WeatherApiClient.retrofit.create(WeatherApiService::class.java)
        val call = apiService.getWeather(cityName, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        val weather = translateWeatherDescription(weatherResponse.weather[0].main) // 단순화된 날씨 정보
                        val description = translateWeatherDescription(weatherResponse.weather[0].description) // 단순화된 설명
                        val temp = weatherResponse.main.temp
                        val humidity = weatherResponse.main.humidity
                        onResult(weather, description, temp, humidity)
                    }
                } else {
                    Toast.makeText(this@GeneralModeActivity, "날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@GeneralModeActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun translateWeatherDescription(description: String): String {
        return when (description.lowercase(Locale.ROOT).trim()) {
            "clear sky","clear" -> "맑은 하늘"

            "few clouds" -> "구름 조금"
            "scattered clouds" -> "드문드문 구름"
            "broken clouds","clouds" -> "흐린 구름"
            "overcast clouds" -> "흐림"

            "light rain" -> "가벼운 비"
            "moderate rain" -> "보통 비"
            "heavy intensity rain" -> "강한 비"
            "very heavy rain" -> "매우 강한 비"
            "extreme rain" -> "극심한 비"
            "freezing rain" -> "어는 비"
            "light intensity shower rain" -> "약한 소나기"
            "shower rain" -> "소나기"
            "heavy intensity shower rain" -> "강한 소나기"
            "ragged shower rain" -> "불규칙한 소나기"

            "light drizzle" -> "약한 이슬비"
            "drizzle" -> "이슬비"
            "heavy drizzle" -> "강한 이슬비"
            "light intensity drizzle rain" -> "약한 이슬비 소나기"
            "drizzle rain" -> "이슬비 소나기"
            "heavy intensity drizzle rain" -> "강한 이슬비 소나기"

            "thunderstorm with light rain" -> "약한 비를 동반한 천둥번개"
            "thunderstorm with rain" -> "비를 동반한 천둥번개"
            "thunderstorm with heavy rain" -> "강한 비를 동반한 천둥번개"
            "light thunderstorm" -> "약한 천둥번개"
            "thunderstorm" -> "천둥번개"
            "heavy thunderstorm" -> "강한 천둥번개"
            "ragged thunderstorm" -> "불규칙한 천둥번개"
            "thunderstorm with light drizzle" -> "약한 이슬비를 동반한 천둥번개"
            "thunderstorm with drizzle" -> "이슬비를 동반한 천둥번개"
            "thunderstorm with heavy drizzle" -> "강한 이슬비를 동반한 천둥번개"

            "light snow" -> "가벼운 눈"
            "snow" -> "눈"
            "heavy snow" -> "많은 눈"
            "sleet" -> "진눈깨비"
            "light shower sleet" -> "약한 소나기 진눈깨비"
            "shower sleet" -> "소나기 진눈깨비"
            "light rain and snow" -> "약한 비와 눈"
            "rain and snow" -> "비와 눈"
            "light shower snow" -> "약한 소나기 눈"
            "shower snow" -> "소나기 눈"
            "heavy shower snow" -> "강한 소나기 눈"

            "mist" -> "옅은 안개"
            "smoke" -> "연기"
            "haze" -> "뿌연 안개"
            "dust" -> "먼지"
            "fog" -> "짙은 안개"
            "sand" -> "모래"
            "volcanic ash" -> "화산재"
            "squall" -> "돌풍"
            "tornado" -> "토네이도"

            else -> description.trim() // 번역이 없는 경우 원본 값을 반환
        }
    }

    private fun getWeatherCharacter(weather: String): Int {
        Log.d("WeatherCharacter", "Received weather: $weather")
        return when (weather.trim()) {
            "맑은 하늘" -> R.drawable.ic_clear
            "구름 조금","드문드문 구름","흐린 구름", "흐림","옅은 안개","연기","뿌연 안개","먼지","짙은 안개","모래","화산재","돌풍","토네이도" -> R.drawable.ic_cloud
            "가벼운 비","보통 비","강한 비","매우 강한 비","극심한 비","어는 비","약한 소나기","소나기","강한 소나기","불규칙한 소나기","약한 이슬비","이슬비","강한 이슬비","약한 이슬비 소나기","이슬비 소나기","강한 이슬비 소나기" -> R.drawable.ic_rain
            "약한 비를 동반한 천둥번개","비를 동반한 천둥번개","강한 비를 동반한 천둥번개","약한 천둥번개","천둥번개","강한 천둥번개","불규칙한 천둥번개","약한 이슬비를 동반한 천둥번개","이슬비를 동반한 천둥번개","강한 이슬비를 동반한 천둥번개" -> R.drawable.ic_thunderstorm
            "가벼운 눈","눈","많은 눈","진눈깨비","약한 소나기 진눈깨비","소나기 진눈깨비","약한 비와 눈","비와 눈","약한 소나기 눈","소나기 눈","강한 소나기 눈" -> R.drawable.ic_snow
            else -> R.drawable.ic_unknown
        }
    }
}
