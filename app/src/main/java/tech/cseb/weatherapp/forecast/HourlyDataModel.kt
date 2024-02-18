package tech.cseb.weatherapp.forecast

data class HourlyDataModel(
    val time: String,
    val imageUrl: String,
    val temp: String,
    val sunrise: String,
    val sunset:String
)