package com.example.soundmap.network
import com.example.soundmap.model.WeatherResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("q") cityName: String, // 도시 이름
        @Query("appid") apiKey: String, // API 키
        @Query("units") units: String = "metric" // 온도 단위 (metric: 섭씨)

    ): Call<WeatherResponse>
}
