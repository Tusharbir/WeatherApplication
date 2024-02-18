package tech.cseb.weatherapp.search

import java.io.Serializable

data class SearchCityItem(
    val country: String,
    val id: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val region: String,
    val url: String
): Serializable

class SearchCity : ArrayList<SearchCityItem>()
