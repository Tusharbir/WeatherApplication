package tech.cseb.weatherapp.utility

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object CalculativeFunctions {

    fun airQualityIndex(value : Int): String{
        return when(value){
            1 -> "Good"
            2-> "Moderate"
            3-> "Unhealthy"
            4-> "Unhealthy"
            5-> "Very Unhealthy"
            else -> "Hazardous"
        }
    }

    fun uvIndexQuality(value: Int): String{
        return if(value in 0..2){
            "Low"
        } else if(value in 3..5){
            "Moderate"
        } else if(value in 6 ..7){
            "High"
        } else if( value in 8 ..10){
            "Very High"
        } else{
            "Extreme"
        }
    }

    fun dateConvertor(epochDate: Long): String{
        val date = Date(epochDate * 1000L)
        val format: DateFormat = SimpleDateFormat("EEEE, d-MMMM-yyyy")
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"))
        return format.format(date)
    }
}