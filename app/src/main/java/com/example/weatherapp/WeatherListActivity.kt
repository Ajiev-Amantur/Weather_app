package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityWeatherListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityWeatherListBinding
    private val cityList = mutableListOf<CityWeather>()
    private lateinit var adapter: CityAdapter
    private lateinit var apiService: WeatherApiService
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupApiService()
        setupRecyclerView()
        setupSearch()
        
        loadDefaultCities()
    }

    private fun setupApiService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(WeatherApiService::class.java)
    }

    private fun setupRecyclerView() {
        adapter = CityAdapter(cityList) { cityName ->
            val resultIntent = Intent()
            resultIntent.putExtra("selected_city", cityName)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        binding.rvWeatherList.layoutManager = LinearLayoutManager(this)
        binding.rvWeatherList.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                val query = s.toString().trim()
                if (query.length >= 2) {
                    searchJob = lifecycleScope.launch {
                        delay(600)
                        searchLocation(query)
                    }
                } else if (query.isEmpty()) {
                    loadDefaultCities()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchCityWeather(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun loadDefaultCities() {
        cityList.clear()
        adapter.notifyDataSetChanged()
        val defaults = listOf("Москва", "Лондон", "Токио", "Нью-Йорк", "Бишкек", "Париж", "Берлин", "Дубай")
        defaults.forEach { searchCityWeather(it) }
    }

    // This performs a "find" to show matching cities as the user types
    private fun searchLocation(query: String) {
        lifecycleScope.launch {
            try {
                // OpenWeatherMap doesn't have a free "autocomplete" but we can search by city name
                // To show a list, we use the "find" endpoint or multiple query results
                // For the purpose of "list selection", we'll clear and show matches
                val response = apiService.getCurrentWeather(query, Constants.API_KEY, Constants.DEFAULT_UNITS, Constants.DEFAULT_LANG)
                if (response.isSuccessful) {
                    val weather = response.body()!!
                    val city = CityWeather(
                        temp = "${weather.main.temp.toInt()}°",
                        city = "${weather.name}, ${weather.sys.country}",
                        condition = weather.weather[0].description.replaceFirstChar { it.uppercase() },
                        range = "М:${weather.main.tempMax.toInt()}°  Н:${weather.main.tempMin.toInt()}°",
                        iconRes = WeatherUtils.getWeatherIcon(weather.weather[0].id, weather.weather[0].icon)
                    )
                    
                    cityList.clear()
                    cityList.add(city)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {}
        }
    }

    private fun searchCityWeather(query: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.getCurrentWeather(query, Constants.API_KEY, Constants.DEFAULT_UNITS, Constants.DEFAULT_LANG)
                if (response.isSuccessful) {
                    val weather = response.body()!!
                    val city = CityWeather(
                        temp = "${weather.main.temp.toInt()}°",
                        city = "${weather.name}, ${weather.sys.country}",
                        condition = weather.weather[0].description.replaceFirstChar { it.uppercase() },
                        range = "М:${weather.main.tempMax.toInt()}°  Н:${weather.main.tempMin.toInt()}°",
                        iconRes = WeatherUtils.getWeatherIcon(weather.weather[0].id, weather.weather[0].icon)
                    )
                    
                    if (!cityList.any { it.city == city.city }) {
                        cityList.add(0, city)
                        adapter.notifyItemInserted(0)
                    }
                }
            } catch (e: Exception) {}
        }
    }
}
