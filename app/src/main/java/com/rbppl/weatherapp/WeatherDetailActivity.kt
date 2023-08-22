package com.rbppl.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rbppl.weatherapp.databinding.ActivityWeatherDetailBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherDetailBinding
    private lateinit var hourlyWeatherAdapter: WeatherDetailAdapter
    private val hourlyWeatherItems = mutableListOf<WeatherItemHourly>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hourlyWeatherAdapter = WeatherDetailAdapter(this, hourlyWeatherItems)
        binding.hourRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.hourRecyclerView.adapter = hourlyWeatherAdapter

        val city = intent.getStringExtra("city")
        val dayIndex = intent.getIntExtra("dayIndex", 0) // Получаем индекс дня из Intent

        // Fetch hourly weather data based on city and day index
        fetchHourlyWeatherData(city.toString(), dayIndex)

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchHourlyWeatherData(city: String, dayIndex: Int) {
        val apiKey = getString(R.string.api_key)
        val url = "https://api.weatherapi.com/v1/forecast.json?key=$apiKey&q=$city&days=5&aqi=no&alerts=no"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                val hourlyWeatherItemsFromAPI = parseHourlyWeatherData(responseData, dayIndex)
                runOnUiThread {
                    hourlyWeatherItems.clear()
                    hourlyWeatherItems.addAll(hourlyWeatherItemsFromAPI)
                    hourlyWeatherAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun parseHourlyWeatherData(responseData: String?, dayIndex: Int): List<WeatherItemHourly> {
        val hourlyWeatherItems = mutableListOf<WeatherItemHourly>()

        try {
            val jsonObject = JSONObject(responseData)
            val hourlyArray = jsonObject.getJSONObject("forecast").getJSONArray("forecastday")
                .getJSONObject(dayIndex).getJSONArray("hour")

            for (i in 0 until hourlyArray.length()) {
                val hourly = hourlyArray.getJSONObject(i)
                val time = hourly.getString("time")
                val condition = hourly.getJSONObject("condition").getString("text")
                val iconUrl = "https:" + hourly.getJSONObject("condition").getString("icon")
                val temperature = hourly.getDouble("temp_c")
                val windSpeed = hourly.getDouble("wind_kph")
                val humidity = hourly.getDouble("humidity")

                val hourlyWeatherItem = WeatherItemHourly(time, condition, iconUrl, temperature, windSpeed, humidity)
                hourlyWeatherItems.add(hourlyWeatherItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return hourlyWeatherItems
    }
}
