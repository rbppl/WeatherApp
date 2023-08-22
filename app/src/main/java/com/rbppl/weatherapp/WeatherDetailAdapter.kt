
package com.rbppl.weatherapp
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rbppl.weatherapp.databinding.ItemWeatherDetailBinding

class WeatherDetailAdapter(private val context: Context, private val hourlyWeatherItems: List<WeatherItemHourly>) :
    RecyclerView.Adapter<WeatherDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeatherDetailBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherItem = hourlyWeatherItems[position]
        holder.bind(weatherItem)
    }

    override fun getItemCount(): Int = hourlyWeatherItems.size

    inner class ViewHolder(private val binding: ItemWeatherDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weatherItem: WeatherItemHourly) {
            binding.timeTextView.text = "Time: ${weatherItem.time}"
            binding.conditionTextView.text = "Condition: ${weatherItem.condition}"
            Glide.with(context)
                .load(weatherItem.iconUrl)
                .into(binding.iconImageView)
            binding.tempTextView.text = "Temperature: ${weatherItem.temperature}°C"
            binding.windTextView.text = "Wind Speed: ${weatherItem.windSpeed} kph"
            binding.humidityTextView.text = "Humidity: ${weatherItem.humidity}%"
        }
    }
}
