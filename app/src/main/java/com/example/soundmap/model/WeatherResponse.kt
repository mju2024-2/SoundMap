package com.example.soundmap.model

data class WeatherResponse(
    val weather: List<Weather>, // 날씨 정보를 담는 리스트
    val main: Main,             // 온도, 습도 등의 주요 정보
    val name: String            // 도시 이름
)

data class Weather(
    val main: String,           // 날씨 상태 (e.g., Clear, Rain)
    val description: String     // 날씨 설명 (e.g., clear sky)
)

data class Main(
    val temp: Float,            // 현재 온도
    val humidity: Int           // 습도
)
