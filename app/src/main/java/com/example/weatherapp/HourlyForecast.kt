package com.example.weatherapp

data class HourlyForecast(
    val time: String,
    val temp: String,
    val iconUrl: String,
    val rainChance: String? = null
)