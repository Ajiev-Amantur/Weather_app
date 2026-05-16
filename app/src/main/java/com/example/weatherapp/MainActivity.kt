package com.example.weatherapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val API_KEY = "YOUR_API_KEY_HERE" // TODO: Add your API key
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // ... previous view compat code ...
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupHourlyForecast()
        fetchWeatherData("Bengaluru")
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        service.getCurrentWeather(city, API_KEY).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { updateUI(it) }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        findViewById<TextView>(R.id.cityName).text = weather.name
        findViewById<TextView>(R.id.currentTemp).text = "${weather.main.temp.toInt()}°"
        findViewById<TextView>(R.id.weatherCondition).text = weather.weather[0].main
        findViewById<TextView>(R.id.tempRange).text = "H:${weather.main.tempMax.toInt()}°  L:${weather.main.tempMin.toInt()}°"
    }

    private fun setupHourlyForecast() {
        val recyclerView = findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        val dummyData = listOf(
            HourlyForecast("12 PM", "19°", android.R.drawable.ic_menu_day),
            HourlyForecast("Now", "20°", android.R.drawable.ic_menu_day),
            HourlyForecast("2 PM", "22°", android.R.drawable.ic_menu_day),
            HourlyForecast("3 PM", "20°", android.R.drawable.ic_menu_day),
            HourlyForecast("4 PM", "19°", android.R.drawable.ic_menu_day),
            HourlyForecast("5 PM", "18°", android.R.drawable.ic_menu_day)
        )

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ForecastAdapter(dummyData)
    }
}