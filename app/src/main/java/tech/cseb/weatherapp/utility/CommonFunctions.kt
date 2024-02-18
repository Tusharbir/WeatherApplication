package tech.cseb.weatherapp.utility

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

object CommonFunctions {

    const val LOCATION_PERMISSION_CODE = 100


    fun getCurrentLocation(context: Context, onComplete:(Location?)->Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            var locationFetched: Location? = null
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        locationFetched = location
                        onComplete(locationFetched)
                    }
                }
        }
    }


    fun customToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getCityName(context: Context, latitude: Double, longitude: Double, onComplete: (String?) -> Unit) {
        var cityName: String? = null
        val geoCoder = Geocoder(context, Locale.getDefault())
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                geoCoder.getFromLocation(latitude, longitude, 1) { address ->
                    if (address.isNotEmpty()) {
                        cityName = address[0].locality
                        onComplete(cityName)
                    }
                }
            }
            else{
                    val address = geoCoder.getFromLocation(latitude,longitude,1)
                    if(address != null) {
                            if (address.isNotEmpty()) {
                                cityName = address[0].locality
                                onComplete(cityName)
                            }
                        }
            }
        } catch (e: Exception) {
            customToast(context, "Something Went Wrong")
        }
    }

}