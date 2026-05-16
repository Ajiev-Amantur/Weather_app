package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ForecastAdapter(private val items: List<HourlyForecast>) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val ivIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)
        val tvTemp: TextView = view.findViewById(R.id.tvHourlyTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTime.text = item.time
        holder.tvTemp.text = item.temp
        holder.ivIcon.setImageResource(item.iconRes)
    }

    override fun getItemCount() = items.size
}