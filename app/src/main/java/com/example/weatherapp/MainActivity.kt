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
    private val API_KEY = "dc29eb2a4e250e8142f207a948997940" // TODO: Add your API key
    
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
        fetchForecastData("Bengaluru")
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

    private fun fetchForecastData(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        service.getForecast(city, API_KEY).enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { updateForecastUI(it) }
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
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

    private fun updateForecastUI(forecast: ForecastResponse) {
        val recyclerView = findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        val hourlyData = forecast.list.take(8).map { item ->
            val time = item.dtTxt.split(" ")[1].substring(0, 5) // Simple extract HH:mm
            HourlyForecast(
                time = time,
                temp = "${item.main.temp.toInt()}°",
                iconRes = android.R.drawable.ic_menu_day // Still placeholder
            )
        }
        recyclerView.adapter = ForecastAdapter(hourlyData)
    }

    private fun setupHourlyForecast() {
        val recyclerView = findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}