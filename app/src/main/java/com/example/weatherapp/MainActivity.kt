package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getLocationWeather()
        } else {
            viewModel.fetchWeather("Москва")
        }
    }

    private val citySelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedCity = result.data?.getStringExtra("selected_city")
            if (selectedCity != null) {
                viewModel.fetchWeather(selectedCity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupUI()
        observeViewModel()

        checkLocationAndStart()
    }

    private fun checkLocationAndStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        } else {
            getLocationWeather()
        }
    }

    private fun setupViewModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherApiService::class.java)
        val repository = WeatherRepository(service)
        viewModel = WeatherViewModel(repository)
    }

    private fun setupUI() {
        binding.hourlyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        
        binding.swipeRefresh.setOnRefreshListener {
            val currentCity = binding.cityName.text.toString()
            if (currentCity != "City" && currentCity.isNotEmpty() && currentCity != "Bengaluru") {
                viewModel.fetchWeather(currentCity)
            } else {
                getLocationWeather()
            }
        }

        binding.btnMyLocation.setOnClickListener { getLocationWeather() }
        binding.btnSearch.setOnClickListener { openSearch() }
        binding.btnList.setOnClickListener { openSearch() }
    }

    private fun openSearch() {
        val intent = Intent(this, WeatherListActivity::class.java)
        citySelectionLauncher.launch(intent)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefresh.isRefreshing = state is WeatherState.Loading
                    when (state) {
                        is WeatherState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.loadingLayout.visibility = View.VISIBLE
                            }
                        }
                        is WeatherState.Success -> {
                            binding.loadingLayout.visibility = View.GONE
                            updateUI(state.current, state.forecast)
                        }
                        is WeatherState.Error -> {
                            binding.loadingLayout.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Ошибка: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> binding.loadingLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun updateUI(current: WeatherResponse, forecast: ForecastResponse) {
        binding.cityName.text = current.name
        binding.currentTemp.text = getString(R.string.temp_format, current.main.temp.toInt())
        binding.weatherCondition.text = current.weather[0].description.replaceFirstChar { it.uppercase() }
        
        val maxTemp = current.main.tempMax.toInt()
        val minTemp = current.main.tempMin.toInt()
        binding.tempRange.text = getString(R.string.temp_range_format, maxTemp, minTemp)
        
        // We use the house for the main icon if it's clear/nice weather, 
        // or can overlay effects on it. For now, matching the reference.
        binding.houseIllustration.setImageResource(R.drawable.house)

        binding.tvSunrise.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(current.sys.sunrise * 1000))
        
        val hourlyData = forecast.list.take(12).map { item ->
            HourlyForecast(
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(item.dt * 1000)),
                temp = getString(R.string.temp_format, item.main.temp.toInt()),
                iconRes = WeatherUtils.getWeatherIcon(item.weather[0].id, item.weather[0].icon),
                rainChance = if (item.pop > 0) "${(item.pop * 100).toInt()}%" else ""
            )
        }
        binding.hourlyRecyclerView.adapter = ForecastAdapter(hourlyData)
    }

    private fun getLocationWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationAndStart()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
            } else {
                getCurrentLocation()
            }
        }.addOnFailureListener {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.fetchWeatherByLocation(location.latitude, location.longitude)
                } else {
                    viewModel.fetchWeather("Москва")
                }
            }
    }
}
