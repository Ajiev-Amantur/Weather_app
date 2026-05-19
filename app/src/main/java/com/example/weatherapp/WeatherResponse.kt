package com.example.weatherapp

import com.google.gson.annotations.SerializedName

// Current Weather Response
data class WeatherResponse(
    @SerializedName("coord") val coord: Coord,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("sys") val sys: Sys,
    @SerializedName("name") val name: String,
    @SerializedName("visibility") val visibility: Int
)

data class Coord(val lat: Double, val lon: Double)
data class Weather(val id: Int, val main: String, val description: String, val icon: String)
data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)
data class Wind(val speed: Double, val deg: Int)
data class Sys(val country: String, val sunrise: Long, val sunset: Long)

// Forecast Response (5 Day / 3 Hour)
data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    @SerializedName("dt_txt") val dtTxt: String,
    @SerializedName("pop") val pop: Double // Probability of precipitation
)

data class City(val name: String, val country: String, val sunrise: Long, val sunset: Long)
