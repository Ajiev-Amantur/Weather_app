package com.example.weatherapp

data class CityWeather(
    val temp: String,
    val city: String,
    val condition: String,
    val range: String,
    val iconRes: Int = R.drawable.weather_sun
)
