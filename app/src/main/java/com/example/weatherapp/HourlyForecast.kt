package com.example.weatherapp

data class HourlyForecast(
    val time: String,
    val temp: String,
    val iconRes: Int, // Local resource ID
    val rainChance: String? = null
)