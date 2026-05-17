package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current,
    @SerializedName("forecast") val forecast: Forecast
)

data class Location(
    @SerializedName("name") val name: String
)

data class Current(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("uv") val uv: Double,
    @SerializedName("air_quality") val airQuality: AirQuality?,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("precip_mm") val precipMm: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("vis_km") val visKm: Double,
    @SerializedName("pressure_mb") val pressureMb: Double
)

data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String
)

data class AirQuality(
    @SerializedName("us-epa-index") val epaIndex: Int
)

data class Forecast(
    @SerializedName("forecastday") val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("day") val day: Day,
    @SerializedName("astro") val astro: Astro,
    @SerializedName("hour") val hour: List<Hour>
)

data class Day(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double
)

data class Astro(
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunset") val sunset: String
)

data class Hour(
    @SerializedName("time") val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("chance_of_rain") val chanceOfRain: Int
)