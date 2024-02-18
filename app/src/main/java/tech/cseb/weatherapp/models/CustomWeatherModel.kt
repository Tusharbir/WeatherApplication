package tech.cseb.weatherapp.models

import java.io.Serializable

data class CustomWeatherModel(
    val cityName: String,
    val country: String,
    val currentHour: Int,
    val temp: String,
    val wind: String,
    val humidity: String,
    val precp: String,
    val weatherCond: String,
    val weatherImageRes: Int,
    val date: String,
    val airQualityIndex: String,
    val airQualityDesc: String,
    val uvIndex: String,
    val uvDesc: String,
): Serializable
