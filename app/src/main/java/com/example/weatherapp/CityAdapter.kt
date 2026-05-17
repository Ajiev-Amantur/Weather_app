package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(private val cities: List<CityWeather>) :
    RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTemp: TextView = view.findViewById(R.id.tvCityTemp)
        val tvName: TextView = view.findViewById(R.id.tvCityName)
        val tvCondition: TextView = view.findViewById(R.id.tvCityCondition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.tvTemp.text = city.temp
        holder.tvName.text = city.city
        holder.tvCondition.text = city.condition
    }

    override fun getItemCount() = cities.size
}