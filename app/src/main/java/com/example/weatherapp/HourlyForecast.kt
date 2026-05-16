package com.example.weatherapp

data class HourlyForecast(
    val time: String,
    val temp: String,
    val iconRes: Int,
    val probability: String? = null
)