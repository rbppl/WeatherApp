package com.rbppl.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rbppl.weatherapp.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherAdapter: WeatherAdapter
    private val weatherItems = mutableListOf<WeatherItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherAdapter = WeatherAdapter(this, weatherItems)
        binding.weatherRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.weatherRecyclerView.adapter = weatherAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length ?: 0 >= 3) {
                    if (isNetworkAvailable()) {
                        fetchWeatherData(s.toString())
                    } else {
                        showNoInternetToast()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchWeatherData(query: String) {
        val apiKey = getString(R.string.api_key)
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=$apiKey&q=$query&days=5&aqi=no&alerts=no"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                val weatherItemsFromAPI = parseWeatherData(responseData)
                val city = parseCityName(responseData)
                val country = parseCountry(responseData)
                runOnUiThread {
                    binding.locationTextView.text = "Location: $city, $country"
                    weatherItems.clear()
                    weatherItems.addAll(weatherItemsFromAPI)
                    weatherAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun parseCityName(responseData: String?): String {
        return try {
            val jsonObject = JSONObject(responseData)
            val locationObject = jsonObject.getJSONObject("location")
            locationObject.getString("name")
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseCountry(responseData: String?): String {
        return try {
            val jsonObject = JSONObject(responseData)
            val locationObject = jsonObject.getJSONObject("location")
            locationObject.getString("country")
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseWeatherData(responseData: String?): List<WeatherItem> {
        val weatherItems = mutableListOf<WeatherItem>()

        try {
            val jsonObject = JSONObject(responseData)
            val forecastArray = jsonObject.getJSONObject("forecast").getJSONArray("forecastday")

            for (i in 0 until forecastArray.length()) {
                val forecast = forecastArray.getJSONObject(i)
                val date = forecast.getString("date")
                val condition = forecast.getJSONObject("day").getJSONObject("condition").getString("text")
                val iconUrl = "https:" + forecast.getJSONObject("day").getJSONObject("condition").getString("icon")
                val temperature = forecast.getJSONObject("day").getDouble("avgtemp_c")
                val windSpeed = forecast.getJSONObject("day").getDouble("maxwind_kph")
                val humidity = forecast.getJSONObject("day").getDouble("avghumidity")

                val weatherItem = WeatherItem(date, condition, iconUrl, temperature, windSpeed, humidity)
                weatherItems.add(weatherItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return weatherItems
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showNoInternetToast() {
        Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
    }
}
