package tech.cseb.weatherapp.api

import tech.cseb.weatherapp.forecast.Forecast
import tech.cseb.weatherapp.models.Weather
import tech.cseb.weatherapp.search.SearchCity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import tech.cseb.weatherapp.fivedays.FiveDayData

interface ApiServices {



    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String= ApiKeyManager.apiKey,
        @Query("q") query: String,
        @Query("aqi") aqi: String = "yes"
    ): Call<Weather>


    @GET("forecast.json")
        fun getCurrentHourlyForecast(
            @Query("q") query: String,
            @Query("days") days: Int = 7,
            @Query("key") apiKey: String= ApiKeyManager.apiKey,
        ): Call<Forecast>

    @GET("search.json")
    fun searchCityCall(
        @Query("key") apiKey: String= ApiKeyManager.apiKey,
        @Query("q") query: String,
    ): Call<SearchCity>



    @GET("forecast")
    fun getFiveDayData(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("appid") apiKey: String = ApiKeyManager.weatherApiKey,
        @Query("units") unit: String = "metric"
    ): Call<FiveDayData>






}