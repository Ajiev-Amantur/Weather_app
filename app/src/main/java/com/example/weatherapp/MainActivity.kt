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

import android.content.Intent
import android.widget.ImageView
import coil.load
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val API_KEY = "17f37714a0cb48d98bf72253261605"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Navigation to List Screen
        findViewById<ImageView>(R.id.btnList).setOnClickListener {
            startActivity(Intent(this, WeatherListActivity::class.java))
        }
        
        // Update date to current
        val sdf = SimpleDateFormat("MMMM, dd", Locale.ENGLISH)
        findViewById<TextView>(R.id.currentDate).text = sdf.format(Date())

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupRecyclerView()
        fetchWeatherData("Bengaluru")
    }

    private fun fetchWeatherData(city: String) {
        // ... (код Retrofit без изменений)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        service.getFullWeather(API_KEY, city).enqueue(object : Callback<WeatherResponse> {
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
        // Update main info
        findViewById<TextView>(R.id.currentTemp).text = "${weather.current.tempC.toInt()}°"
        
        // Load main large icon
        val mainIconUrl = "https:${weather.current.condition.icon.replace("64x64", "128x128")}"
        findViewById<ImageView>(R.id.mainWeatherIcon).load(mainIconUrl)
        
        val maxTemp = weather.forecast.forecastDay[0].day.maxTempC.toInt()
        val minTemp = weather.forecast.forecastDay[0].day.minTempC.toInt()
        findViewById<TextView>(R.id.tempRange).text = "Max: $maxTemp°  Min: $minTemp°"

        // Update Hourly Forecast
        val hourlyData = weather.forecast.forecastDay[0].hour.map { hour ->
            val timeOnly = hour.time.split(" ")[1] // Gets "HH:mm" from "yyyy-MM-dd HH:mm"
            HourlyForecast(
                time = timeOnly,
                temp = "${hour.tempC.toInt()}°",
                iconUrl = hour.condition.icon,
                rainChance = "${hour.chanceOfRain}%"
            )
        }.take(24) // Show next 24 hours

        findViewById<RecyclerView>(R.id.hourlyRecyclerView).adapter = ForecastAdapter(hourlyData)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.hourlyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}