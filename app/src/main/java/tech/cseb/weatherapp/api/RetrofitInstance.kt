package tech.cseb.weatherapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {

    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    private const val BASE_URL_WEATHER_API = "https://api.openweathermap.org/data/2.5/"



    val api: ApiServices = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiServices::class.java)

    val weatherAPI:ApiServices = Retrofit.Builder()
        .baseUrl(BASE_URL_WEATHER_API)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiServices::class.java)

}