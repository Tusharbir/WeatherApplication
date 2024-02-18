package tech.cseb.weatherapp

import android.content.Context
import android.util.Log
import tech.cseb.weatherapp.api.RetrofitInstance
import tech.cseb.weatherapp.forecast.Forecast
import tech.cseb.weatherapp.forecast.HourlyDataModel
import tech.cseb.weatherapp.forecast.WeeklyDataModelClass
import tech.cseb.weatherapp.models.CustomWeatherModel
import tech.cseb.weatherapp.models.Weather
import tech.cseb.weatherapp.search.SearchCity
import tech.cseb.weatherapp.search.SearchCityItem
import tech.cseb.weatherapp.utility.CalculativeFunctions
import tech.cseb.weatherapp.utility.CommonFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.cseb.weatherapp.fivedays.FiveDayData
import tech.cseb.weatherapp.utility.CalculativeFunctions.dateConvertor
import java.time.Instant
import java.time.ZoneId

class Repository {


    fun searchCity(text: String, onComplete:(ArrayList<SearchCityItem>)-> Unit){
        val cityList= arrayListOf<SearchCityItem>()
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.api.searchCityCall(query = text).enqueue(object : Callback<SearchCity>{
                override fun onResponse(call: Call<SearchCity>, response: Response<SearchCity>) {
                    if(response.isSuccessful){
                        val cityNames = response.body()
                        if(cityNames!=null){
                            cityNames.forEach {
                                cityName -> if(!cityList.contains(cityName)){
                                    cityList.add(cityName)
                            }
                                onComplete(cityList)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SearchCity>, t: Throwable) {

                }

            })
        }
    }


    suspend fun fetchWeatherData(context: Context, query: String, onComplete: (CustomWeatherModel, ArrayList<HourlyDataModel>, ArrayList<WeeklyDataModelClass>) -> Unit){
    var weatherModel: CustomWeatherModel? = null
    val job = CoroutineScope(Dispatchers.Main).async {
        RetrofitInstance.api.getCurrentWeather(query = query)
            .enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            val cityName = weatherData.location.name
                            val country = weatherData.location.country
                            val currentHour = if(weatherData.location.localtime.substring(11,13).contains(":", true)){
                                weatherData.location.localtime.substring(11,12).toInt()
                            }
                            else{
                                weatherData.location.localtime.substring(11,13).toInt()
                            }
                            val currentTemp = "${weatherData.current.temp_c}°"
                            val wind = "${weatherData.current.wind_kph} km/h"
                            val humidity = "${weatherData.current.humidity} %"
                            val precp = "${weatherData.current.precip_mm} mm"

                            val weatherCondition = weatherData.current.condition.text

                            val weatherImageRes = if (weatherData.current.condition.text.contains("cloudy", true)) {
                                R.drawable.ic_cloudy_sunny
                            } else if (weatherData.current.condition.text.contains(
                                    "rain",
                                    true
                                )
                            ) {
                                R.drawable.ic_thunderstorm
                            } else if (weatherData.current.condition.text.contains(
                                    "snow",
                                    true
                                )
                            ) {
                                R.drawable.ic_snow
                            } else if (weatherData.current.condition.text.contains(
                                    "rain sun",
                                    true
                                )
                            ) {
                                R.drawable.ic_rainy_sunny
                            }
                            else if(weatherData.current.condition.text.contains("overcast", true)){
                                R.drawable.ic_overcast
                            }
                            else if(weatherData.current.condition.text.contains("mist", true)){
                                R.drawable.ic_mist
                            }
                            else {
                                R.drawable.ic_cloudy_sunny
                            }

                            val epochDate = weatherData.location.localtime_epoch.toLong()
                            val formattedDate = CalculativeFunctions.dateConvertor(epochDate)


                            val airQuality = weatherData.current.air_quality.usEpaIndex
                            val airQualityIndex = "Index: $airQuality"
                            val airQualityDesc = CalculativeFunctions.airQualityIndex(airQuality)

                            val uvIndex = weatherData.current.uv.toInt()
                            val uvDesc = CalculativeFunctions.uvIndexQuality(uvIndex)

                            weatherModel = CustomWeatherModel(cityName,country,currentHour,currentTemp,wind,humidity,precp,weatherCondition,
                                weatherImageRes,formattedDate, airQualityIndex, airQualityDesc, uvIndex.toString(), uvDesc)
                        }
                    } else {
                        CommonFunctions.customToast(context, "Unexpected Thing Occurred")
                    }
                }
                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    CommonFunctions.customToast(context, "Unexpected Thing Occurred")
                }
            }
            )
    }
    job.await()
    val hourlyJob = CoroutineScope(Dispatchers.IO).async {
        getHourlyForecast(query){
            hourlyData, weekData ->
            weatherModel?.let { it1 -> onComplete(it1,hourlyData, weekData) }
    }
    }
    hourlyJob.await()


}

     private suspend fun getHourlyForecast(query: String, onComplete: (ArrayList<HourlyDataModel>, ArrayList<WeeklyDataModelClass> ) -> Unit){
         var sevenDaysData: ArrayList<WeeklyDataModelClass>
         RetrofitInstance.api.getCurrentHourlyForecast(query = query).enqueue(object: Callback<Forecast>{
                override fun onResponse(call: Call<Forecast>, response: Response<Forecast>) {
                    if(response.isSuccessful){
                        val hourlyData = response.body()
                        if(hourlyData!=null){

                            val lat = hourlyData.location.lat
                            val long = hourlyData.location.lon

                            fiveDaysData(lat,long){
                                weekDataList ->
                                sevenDaysData = weekDataList
//

                                val forecastHours = hourlyData.forecast.forecastday[0].hour

                                val sunrise = hourlyData.forecast.forecastday[0].astro.sunrise
                                val sunset = hourlyData.forecast.forecastday[0].astro.sunset
                                val newHourlyDataList = ArrayList<HourlyDataModel>()

                                forecastHours.forEach { forecastHour ->
                                    val hour = forecastHour.time.substring(11, 13).toInt()

                                    val amPm = if (hour < 12) "AM" else "PM"

                                    val formattedHour = if(hour==0) "12"   else if (hour <= 12) hour.toString() else (hour - 12).toString()

                                    val icon = "https://${forecastHour.condition.icon}"
                                    val temp = "${forecastHour.temp_c}°"



                                    val newHourlyData = HourlyDataModel("$formattedHour $amPm", icon, temp, sunrise, sunset)
                                    newHourlyDataList.add(newHourlyData)
                                }
                                onComplete(newHourlyDataList, sevenDaysData)

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Forecast>, t: Throwable) {

                }
            })

    }



    fun fiveDaysData(lat: Double, long: Double,onComplete: (ArrayList<WeeklyDataModelClass>) -> Unit){

        val weeklyDataList = arrayListOf<WeeklyDataModelClass>()

        RetrofitInstance.weatherAPI.getFiveDayData(lat.toString(),long.toString()).enqueue(object :Callback<FiveDayData>{
            override fun onResponse(
                call: Call<FiveDayData>,
                response: Response<FiveDayData>
            ) {


               if(response.isSuccessful){
                   val data = response.body()
                   if(data!=null){
                       val groupedData = data.list.groupBy {
                           Instant.ofEpochSecond(it.dt.toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
                       }
                        groupedData.forEach{date, foreacast ->
                            val maxtemp = "${foreacast.maxOf { it.main.temp_max }}°"
                            val mintemp = "${foreacast.minOf { it.main.temp_min }}°"
                            val day  = date.dayOfWeek.toString()
                            val iconUrl = "https://openweathermap.org/img/wn/${foreacast.first().weather.first().icon}@2x.png"
                            weeklyDataList.add(WeeklyDataModelClass(day, iconUrl, maxtemp, mintemp))
                        }
                       onComplete(weeklyDataList)
                   }
               }
            }

            override fun onFailure(call: Call<FiveDayData>, t: Throwable) {
            }

        })
    }


}