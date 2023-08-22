package com.rbppl.weatherapp
data class WeatherItem(
    val date: String,
    val condition: String,
    val iconUrl: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Double,
    val city: String,
    val dayIndex: Int // Добавляем индекс дня
)
