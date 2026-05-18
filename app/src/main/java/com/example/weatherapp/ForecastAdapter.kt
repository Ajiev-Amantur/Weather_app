package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemHourlyBinding

class ForecastAdapter(private val items: List<HourlyForecast>) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHourlyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvTime.text = item.time
            tvHourlyTemp.text = item.temp
            tvRainChance.text = item.rainChance
            ivWeatherIcon.setImageResource(item.iconRes)
        }
    }

    override fun getItemCount() = items.size
}