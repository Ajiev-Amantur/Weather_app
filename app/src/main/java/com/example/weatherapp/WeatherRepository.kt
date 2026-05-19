package com.example.weatherapp

import retrofit2.Response

class WeatherRepository(private val apiService: WeatherApiService) {

    suspend fun getCurrentWeather(city: String): Response<WeatherResponse> {
        return apiService.getCurrentWeather(city, Constants.API_KEY)
    }

    suspend fun getWeatherByLocation(lat: Double, lon: Double): Response<WeatherResponse> {
        return apiService.getWeatherByLocation(lat, lon, Constants.API_KEY)
    }

    suspend fun getForecast(city: String): Response<ForecastResponse> {
        return apiService.getForecast(city, Constants.API_KEY)
    }
}
