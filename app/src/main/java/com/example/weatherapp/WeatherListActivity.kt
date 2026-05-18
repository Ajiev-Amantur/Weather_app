package com.example.weatherapp

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityWeatherListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherListActivity : AppCompatActivity() {
    private val apiKey = "17f37714a0cb48d98bf72253261605"
    private lateinit var binding: ActivityWeatherListBinding
    private val cityList = mutableListOf<CityWeather>()
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CityAdapter(cityList) { finish() }
        
        binding.rvWeatherList.apply {
            layoutManager = LinearLayoutManager(this@WeatherListActivity)
            adapter = this@WeatherListActivity.adapter
        }

        binding.searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCity(v.text.toString())
                true
            } else {
                false
            }
        }
        
        listOf("London", "Tokyo", "New York").forEach { searchCity(it) }
    }

    private fun searchCity(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        service.getFullWeather(apiKey, cityName).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val city = CityWeather("${it.current.tempC.toInt()}°", it.location.name, it.current.condition.text)
                        cityList.add(0, city)
                        adapter.notifyItemInserted(0)
                        binding.rvWeatherList.scrollToPosition(0)
                    }
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@WeatherListActivity, "Search failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
