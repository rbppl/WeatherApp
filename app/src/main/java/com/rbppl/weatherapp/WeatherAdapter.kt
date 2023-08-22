package com.rbppl.weatherapp
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rbppl.weatherapp.databinding.ItemWeatherForecastBinding

class WeatherAdapter(private val context: Context, private val weatherItems: List<WeatherItem>) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWeatherForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = weatherItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return weatherItems.size
    }

    inner class ViewHolder(private val binding: ItemWeatherForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = weatherItems[position]
                    val intent = Intent(context, WeatherDetailActivity::class.java)
                    intent.putExtra("city", item.city)
                    intent.putExtra("dayIndex", item.dayIndex) // Передаем индекс дня
                    context.startActivity(intent)
                }
            }
        }
        fun bind(weatherItem: WeatherItem) {
            binding.dateTextView.text = "Date: ${weatherItem.date}"
            binding.conditionTextView.text = "Condition: ${weatherItem.condition}"
            Glide.with(context).load(weatherItem.iconUrl).into(binding.iconImageView)
            binding.tempTextView.text = "Temperature: ${weatherItem.temperature}°C"
            binding.windTextView.text = "Wind Speed: ${weatherItem.windSpeed} kph"
            binding.humidityTextView.text = "Humidity: ${weatherItem.humidity}%"
        }
    }
}
