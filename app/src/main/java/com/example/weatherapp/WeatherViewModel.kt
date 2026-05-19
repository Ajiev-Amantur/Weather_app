package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val current: WeatherResponse, val forecast: ForecastResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
    object Empty : WeatherState()
}

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val uiState: StateFlow<WeatherState> = _uiState

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _uiState.value = WeatherState.Loading
            try {
                val currentResponse = repository.getCurrentWeather(city)
                val forecastResponse = repository.getForecast(city)

                if (currentResponse.isSuccessful && forecastResponse.isSuccessful) {
                    _uiState.value = WeatherState.Success(currentResponse.body()!!, forecastResponse.body()!!)
                } else {
                    _uiState.value = WeatherState.Error("Ошибка загрузки данных")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherState.Loading
            try {
                val currentResponse = repository.getWeatherByLocation(lat, lon)
                if (currentResponse.isSuccessful) {
                    val city = currentResponse.body()!!.name
                    val forecastResponse = repository.getForecast(city)
                    if (forecastResponse.isSuccessful) {
                        _uiState.value = WeatherState.Success(currentResponse.body()!!, forecastResponse.body()!!)
                    } else {
                        _uiState.value = WeatherState.Error("Ошибка загрузки прогноза")
                    }
                } else {
                    _uiState.value = WeatherState.Error("Ошибка определения города")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}
