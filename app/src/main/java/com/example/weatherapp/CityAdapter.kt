package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemWeatherCityBinding

class CityAdapter(
    private val cities: List<CityWeather>,
    private val onCitySelected: (String) -> Unit
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemWeatherCityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWeatherCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.binding.apply {
            tvCityTemp.text = city.temp
            tvCityName.text = city.city
            tvCityCondition.text = city.condition
            root.setOnClickListener { onCitySelected(city.city) }
        }
    }

    override fun getItemCount() = cities.size
}