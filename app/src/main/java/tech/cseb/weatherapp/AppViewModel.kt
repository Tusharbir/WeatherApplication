package tech.cseb.weatherapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.cseb.weatherapp.forecast.HourlyDataModel
import tech.cseb.weatherapp.forecast.WeeklyDataModelClass
import tech.cseb.weatherapp.models.CustomWeatherModel
import tech.cseb.weatherapp.search.SearchCityItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    val repository = Repository()

    private val _fetchedCityNames = MutableLiveData<ArrayList<SearchCityItem>>()
    val fetchedCityNames: LiveData<ArrayList<SearchCityItem>> = _fetchedCityNames

    fun searchCityNames(text: String) {
        repository.searchCity(text) { cityNames ->
            if (cityNames.isEmpty()) {
                _fetchedCityNames.postValue(arrayListOf())
            } else {
                _fetchedCityNames.postValue(cityNames)
            }
        }
    }


    fun fetchWeatherByLatLong(
        context: Context,
        query: String,
        onComplete: (CustomWeatherModel, ArrayList<HourlyDataModel>, ArrayList<WeeklyDataModelClass>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.fetchWeatherData(context, query) { cityDataModel, hourlyModel, weekData ->
                onComplete(cityDataModel, hourlyModel, weekData)
            }
        }

    }

}