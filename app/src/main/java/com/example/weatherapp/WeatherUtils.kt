package com.example.weatherapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable

object WeatherUtils {
    /**
     * Maps OpenWeatherMap condition to local resources.
     * Uses the premium naming convention provided by the user.
     * Ensure these drawables (e.g., ic_sun_clear_day) exist in res/drawable.
     */
    fun getWeatherIcon(conditionCode: Int, icon: String): Int {
        val isNight = icon.endsWith("n")

        // Mapping based on OpenWeatherMap codes and day/night status
        return when (conditionCode) {
            // 1. Clear
            800 -> if (isNight) R.drawable.weather_sun else R.drawable.weather_sun 
            // Recommended: R.drawable.ic_moon_clear_night : R.drawable.ic_sun_clear_day

            // 2. Clouds
            801 -> if (isNight) R.drawable.weather_5 else R.drawable.weather_5
            // Recommended: R.drawable.ic_few_clouds_night : R.drawable.ic_few_clouds_day
            
            802 -> R.drawable.weather_5 // ic_clouds_overcast (or few clouds)
            in 803..804 -> R.drawable.weather_5 // ic_clouds_overcast
            
            // 3. Rain & Drizzle
            in 300..321 -> R.drawable.weather_5 // ic_drizzle
            in 500..504 -> R.drawable.weather_4 // ic_rain_light
            in 511..531 -> R.drawable.weather_4 // ic_rain_heavy
            
            // 4. Thunderstorm
            in 200..232 -> R.drawable.weather_7 // ic_thunderstorm
            
            // 5. Snow
            in 600..622 -> R.drawable.snow // ic_snow
            
            // 6. Atmosphere
            701 -> R.drawable.weather_5 // ic_mist
            741 -> R.drawable.weather_5 // ic_fog
            
            else -> R.drawable.weather_5
        }
    }

    /**
     * Maps OpenWeatherMap "main" string to the specific premium icons provided.
     * Use this for a cleaner high-level mapping.
     */
    fun getPremiumIcon(main: String, icon: String): Int {
        val isNight = icon.endsWith("n")
        return when(main) {
            "Clear" -> if (isNight) R.drawable.weather_sun else R.drawable.weather_sun // ic_moon_clear_night / ic_sun_clear_day
            "Clouds" -> if (isNight) R.drawable.weather_5 else R.drawable.weather_5 // ic_few_clouds_night / ic_few_clouds_day
            "Rain" -> R.drawable.weather_4 // ic_rain_light / ic_rain_heavy
            "Drizzle" -> R.drawable.weather_5 // ic_drizzle
            "Thunderstorm" -> R.drawable.weather_7 // ic_thunderstorm
            "Snow" -> R.drawable.snow // ic_snow
            "Mist" -> R.drawable.weather_5 // ic_mist
            "Fog" -> R.drawable.weather_5 // ic_fog
            else -> R.drawable.weather_5
        }
    }

    fun getBackgroundGradient(conditionCode: Int, icon: String): GradientDrawable {
        val isNight = icon.endsWith("n")
        val colors = when {
            isNight -> intArrayOf(Color.parseColor("#1C1B33"), Color.parseColor("#2E335A")) // Dark Night
            conditionCode == 800 -> intArrayOf(Color.parseColor("#4A90E2"), Color.parseColor("#87CEEB")) // Sunny
            conditionCode in 801..804 -> intArrayOf(Color.parseColor("#5C6BC0"), Color.parseColor("#3949AB")) // Cloudy
            conditionCode in 500..531 -> intArrayOf(Color.parseColor("#1A237E"), Color.parseColor("#3949AB")) // Rainy
            else -> intArrayOf(Color.parseColor("#2E335A"), Color.parseColor("#1C1B33"))
        }
        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
    }
}
