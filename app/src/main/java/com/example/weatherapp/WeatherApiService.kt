package com.example.weatherapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = Constants.DEFAULT_UNITS,
        @Query("lang") lang: String = Constants.DEFAULT_LANG
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = Constants.DEFAULT_UNITS,
        @Query("lang") lang: String = Constants.DEFAULT_LANG
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = Constants.DEFAULT_UNITS,
        @Query("lang") lang: String = Constants.DEFAULT_LANG
    ): Response<ForecastResponse>
}
