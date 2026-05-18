package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
i   mport android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val apiKey = "17f37714a0cb48d98bf72253261605"
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnList.setOnClickListener {
            startActivity(Intent(this, WeatherListActivity::class.java))
        }

        binding.houseIllustration.setOnClickListener { getLocationWeather() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupRecyclerView()
        if (checkLocationPermissions()) getLocationWeather() else fetchWeatherData("Bengaluru")
    }

    private fun checkLocationPermissions() = ActivityCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun getLocationWeather() {
        if (!checkLocationPermissions()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1002)
            return
        }
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    fetchWeatherData("${location.latitude},${location.longitude}")
                } else {
                    fetchWeatherData("Bengaluru")
                }
            }
        } catch (e: SecurityException) {
            fetchWeatherData("Bengaluru")
        }
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        service.getFullWeather(apiKey, city).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) response.body()?.let { updateUI(it) }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        binding.apply {
            cityName.text = weather.location.name
            currentTemp.text = getString(R.string.temp_format, weather.current.tempC.toInt())
            weatherCondition.text = weather.current.condition.text
            
            val maxTemp = weather.forecast.forecastDay[0].day.maxTempC.toInt()
            val minTemp = weather.forecast.forecastDay[0].day.minTempC.toInt()
            tempRange.text = getString(R.string.temp_range_format, maxTemp, minTemp)

            mainWeatherIcon.setImageResource(WeatherUtils.getWeatherIcon(weather.current.condition.code))

            val hourlyData = weather.forecast.forecastDay[0].hour.map { hour ->
                HourlyForecast(
                    time = hour.time.split(" ")[1],
                    temp = "${hour.tempC.toInt()}°",
                    iconRes = WeatherUtils.getWeatherIcon(hour.condition.code),
                    rainChance = if (hour.chanceOfRain > 0) "${hour.chanceOfRain}%" else ""
                )
            }.take(24)

            hourlyRecyclerView.adapter = ForecastAdapter(hourlyData)
            
            tvUVIndex.text = weather.current.uv.toInt().toString()
            tvSunrise.text = weather.forecast.forecastDay[0].astro.sunrise
            tvHumidity.text = "${weather.current.humidity}%"
            tvWind.text = "${weather.current.windKph} km/h"
            tvRainfall.text = "${weather.current.precipMm} mm"
            
            weather.current.airQuality?.let {
                tvAirQuality.text = when(it.epaIndex) {
                    1 -> "1-Excellent"
                    2 -> "2-Good"
                    3 -> "3-Moderate"
                    else -> "Low Risk"
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.hourlyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1002 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getLocationWeather()
    }
}