package com.rbppl.weatherapp

data class WeatherItem(
    var date: String = "",
    var condition: String = "",
    var iconUrl: String = "",
    var temperature: Double,
    var windSpeed: Double,
    var humidity: Double
)
