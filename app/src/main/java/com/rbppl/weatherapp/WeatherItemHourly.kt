package com.rbppl.weatherapp
data class WeatherItemHourly(
    val time: String,
    val condition: String,
    val iconUrl: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Double
)
