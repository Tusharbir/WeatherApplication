package tech.cseb.weatherapp.utility

import android.content.Context

object SharedPreferences {

    private const val cityDetailsFileName : String = "cityDetailsFile"
    private const val counterFile : String = "counterFile"

    private const val counterData: String = "counterData"
    private const val cityDetailsData1: String = "cityDetailsData1"
    private const val cityDetailsData2: String = "cityDetailsData2"
    private const val cityDetailsData3: String = "cityDetailsData3"


    public fun getCityData1(context: Context?): String? {
        val sharedPreferences = context?.getSharedPreferences(cityDetailsFileName, Context.MODE_PRIVATE)
        val city1 = sharedPreferences?.getString(cityDetailsData1, null)
        return city1
    }

    public fun getCityData2(context: Context?): String? {
        val sharedPreferences = context?.getSharedPreferences(cityDetailsFileName, Context.MODE_PRIVATE)
        val city2 = sharedPreferences?.getString(cityDetailsData2, null)
        return city2
    }

    public fun getCityData3(context: Context?): String? {
        val sharedPreferences = context?.getSharedPreferences(cityDetailsFileName, Context.MODE_PRIVATE)
        val city3 = sharedPreferences?.getString(cityDetailsData3, null)
        return city3
    }



    public fun saveData1(context: Context, cityLatLong: String){
        val sharedPreferences = context.getSharedPreferences(cityDetailsFileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(cityDetailsData1, cityLatLong)
        editor.apply()
    }

    public fun saveData2(context: Context, cityLatLong: String){
        val sharedPreferences = context.getSharedPreferences(cityDetailsFileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(cityDetailsData2, cityLatLong)
        editor.apply()
    }

    public fun saveData3(context: Context, cityLatLong: String){
        val sharedPreferences = context.getSharedPreferences(cityDetailsFileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(cityDetailsData3, cityLatLong)
        editor.apply()
    }

    public fun updateCounter(context: Context){
        val sharedPreferences = context.getSharedPreferences(counterFile, Context.MODE_PRIVATE)
        var counterValue = getCounterData(context)
        counterValue = if (counterValue >= 3) 1 else counterValue + 1
        val editor = sharedPreferences.edit()
        editor.putInt(counterData, counterValue)
        editor.apply()
    }

    public fun getCounterData(context: Context): Int{
        val sharedPreferences = context.getSharedPreferences(counterFile, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(counterData, 0)
    }




}