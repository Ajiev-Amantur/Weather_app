package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WeatherListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rvWeatherList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Mock data for Screen 3
        val mockCities = listOf(
            CityWeather("19°", "Bengaluru, India", "Mid Rain"),
            CityWeather("22°", "Chennai, India", "Fast Wind"),
            CityWeather("29°", "Delhi, India", "Cloudy")
        )
        
        recyclerView.adapter = CityAdapter(mockCities)
    }
}

data class CityWeather(val temp: String, val city: String, val condition: String)
