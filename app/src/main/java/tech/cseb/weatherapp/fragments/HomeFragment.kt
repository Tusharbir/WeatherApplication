package tech.cseb.weatherapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import tech.cseb.weatherapp.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.cseb.weatherapp.R
import tech.cseb.weatherapp.adapters.AdapterHourlyWeather
import tech.cseb.weatherapp.adapters.AdapterSearchCity
import tech.cseb.weatherapp.adapters.AdapterWeeklyForecast
import tech.cseb.weatherapp.databinding.FragmentHomeBinding
import tech.cseb.weatherapp.utility.CommonFunctions.LOCATION_PERMISSION_CODE
import tech.cseb.weatherapp.utility.CommonFunctions.customToast
import tech.cseb.weatherapp.utility.CommonFunctions.getCityName
import tech.cseb.weatherapp.utility.CommonFunctions.getCurrentLocation
import tech.cseb.weatherapp.utility.SharedPreferences.getCounterData
import tech.cseb.weatherapp.utility.SharedPreferences.saveData1
import tech.cseb.weatherapp.utility.SharedPreferences.saveData2
import tech.cseb.weatherapp.utility.SharedPreferences.saveData3
import tech.cseb.weatherapp.utility.SharedPreferences.updateCounter
import tech.cseb.weatherapp.utility.loading_utils.LoadingUtils


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var query: String


    private lateinit var searchAdapter: AdapterSearchCity

    private val viewModel = AppViewModel()
    private lateinit var sunriseAnimation : LottieAnimationView
    private lateinit var sunsetAnimation : LottieAnimationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.progressBar.visibility= View.VISIBLE

        sunriseAnimation = binding.animSunrise
        sunsetAnimation = binding.animSunset

        checkLocationServicesAndPermissions()

        observeSearchStatus()

        searchAdapter = AdapterSearchCity() { city ->
            val latLong = "${city.lat}, ${city.lon}"

            CoroutineScope(Dispatchers.IO).launch {
                this@HomeFragment.context?.applicationContext?.let { context ->
                    val counter = getCounterData(context)
                    when (counter) {
                        0, 3 -> saveData1(context, latLong)
                        1 -> saveData2(context, latLong)
                        2 -> saveData3(context, latLong)
                    }
                    updateCounter(context)

                }
            }

            val bundle = Bundle()
            bundle.putSerializable("cityData", city)
            val fragment = SearchedCityFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(
                HomeFragment::class.java.toString()
            ).commit()

        }



        binding.recyclerCitySearchNames.layoutManager = LinearLayoutManager(this.context?.applicationContext)
        binding.recyclerCitySearchNames.adapter = searchAdapter

        binding.svSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        binding.recyclerCitySearchNames.visibility = View.GONE
                    }
                    viewModel.searchCityNames(it)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        LoadingUtils.showDialog(requireContext(),false)
        checkLocationServicesAndPermissions()
        Handler(Looper.getMainLooper()).postDelayed({
            LoadingUtils.hideDialog()
        }, 2000)
    }




    private fun observeSearchStatus() {
        viewModel.fetchedCityNames.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.recyclerCitySearchNames.visibility = View.VISIBLE
                searchAdapter.updateSearchData(it)
            }
        }
    }

    private fun areLocationServicesEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
    }

    private fun checkLocationServicesAndPermissions() {
        if (!areLocationServicesEnabled()) {
            AlertDialog.Builder(requireContext())
                .setMessage("Location services are disabled. Would you like to enable them?")
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                    binding.icNoLocation.visibility=View.VISIBLE
                    binding.tvNoLocationError.visibility=View.VISIBLE
                    binding.tvNoLocationMessage.visibility=View.VISIBLE

                }
                .create()
                .show()

        } else {
            checkPermissionsAndFetchLocation()
        }
    }



    private fun checkPermissionsAndFetchLocation(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_CODE)
        }
        else{
            getLocation(){
                getCityName(Pair(it.first,it.second)){
                    city ->
                    binding.tvCityName.text = city
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation(){
                        getCityName(Pair(it.first,it.second)){
                                city ->
                            binding.tvCityName.text = city
                        }
                    }
                } else {
                    customToast(
                        requireContext(),
                        "Permission Denied"
                    )
                }
            }
        }
    }

    fun getLocation(onComplete:(Pair<Double?, Double?>)->Unit){
        var longitude: Double?
        var latitude: Double?
        this.context?.let {
            context ->
            getCurrentLocation(context.applicationContext){
                location ->
                if(location!=null) {
                latitude = location.latitude
                longitude = location.longitude
                query = "$latitude,$longitude"
                fetchAndSetData(context.applicationContext, query)
                onComplete(Pair(latitude,longitude))
            }
            }
        }
    }

    private fun getCityName(location : Pair<Double?, Double?>, onComplete: (String?) -> Unit){
        if(location.first !=null && location.second!=null){
            this.context?.let {
                getCityName(it.applicationContext, location.first!!, location.second!!)
                { city ->
                    onComplete(city)
                }
            }
        }
    }

    private fun fetchAndSetData(context: Context, query: String) {
        viewModel.fetchWeatherByLatLong(context, query) { weatherData, hourlyData, weekData ->

            val topAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_top)
            val bottomAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_bottom)
            val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scaling_animation)


            binding.tvDegrees.text = weatherData.temp





            binding.tvWindSpeed.text = weatherData.wind
            binding.tvHumidity.text = weatherData.humidity
            binding.tvRain.text = weatherData.precp

            binding.tvWeatherStatus.text = weatherData.weatherCond

            binding.ivWeatherImage.setImageResource(weatherData.weatherImageRes)

            binding.tvDate.text = weatherData.date

            binding.tvAirQualityIndex.text = weatherData.airQualityIndex
            binding.tvTvAirQualityDesc.text = weatherData.airQualityDesc

            binding.tvUvIndex.text = weatherData.uvIndex
            binding.tvUvIndexDesc.text = weatherData.uvDesc

            binding.tvSunrise.text =hourlyData[0].sunrise
            binding.tvSunset.text =hourlyData[0].sunset

            val hourlyAdapter = AdapterHourlyWeather()
            hourlyAdapter.updateHourlyData(hourlyData)
            binding.recyclerHourly.layoutManager = LinearLayoutManager(
                this.context?.applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerHourly.adapter = hourlyAdapter
            if(weatherData.currentHour in 3..21){
                binding.recyclerHourly.scrollToPosition(weatherData.currentHour-2)
            }
            else if(weatherData.currentHour>21){
                binding.recyclerHourly.scrollToPosition(19)
            }


            val weeklyAdapter = AdapterWeeklyForecast()

            weeklyAdapter.updateWeeklyData(weekData.subList(1,weekData.size))

            binding.recyclerWeekly.layoutManager =
                LinearLayoutManager(this.context?.applicationContext)
            binding.recyclerWeekly.adapter = weeklyAdapter


            binding.icNoLocation.visibility=View.GONE
            binding.tvNoLocationError.visibility=View.GONE
            binding.tvNoLocationMessage.visibility=View.GONE

            binding.tvCityName.visibility=View.VISIBLE
            binding.tvDate.visibility=View.VISIBLE
            binding.tvDegrees.visibility=View.VISIBLE
            binding.ivWeatherImage.visibility=View.VISIBLE
            binding.tvWeatherStatus.visibility=View.VISIBLE
            binding.cvStats.visibility=View.VISIBLE
            binding.cvAirQuality.visibility=View.VISIBLE
            binding.cvUv.visibility=View.VISIBLE
            binding.cvHourlyForecast.visibility=View.VISIBLE
            binding.cvSevenDayForecast.visibility=View.VISIBLE
            binding.cvSunrise.visibility=View.VISIBLE
            binding.cvSunset.visibility=View.VISIBLE


            binding.tvDegrees.startAnimation(topAnimation)
            binding.tvCityName.startAnimation(topAnimation)
            binding.tvDate.startAnimation(topAnimation)
            binding.svSearchView.startAnimation(topAnimation)
            binding.ivWeatherImage.startAnimation(scaleAnimation)

            binding.cvStats.startAnimation(bottomAnimation)
            binding.cvAirQuality.startAnimation(bottomAnimation)
            binding.cvUv.startAnimation(bottomAnimation)
            binding.cvHourlyForecast.startAnimation(bottomAnimation)
            binding.cvSevenDayForecast.startAnimation(bottomAnimation)
            binding.cvSunrise.startAnimation(bottomAnimation)
            binding.cvSunset.startAnimation(bottomAnimation)
            handleSunriseAnimation()
            handleSunsetAnimation()
            binding.progressBar.visibility= View.GONE



        }
    }

    private fun handleSunriseAnimation(){
        val startFrame = 200
        val stopFrame = 1200
        sunriseAnimation.setMinAndMaxFrame(startFrame, stopFrame)
        sunriseAnimation.speed = 3f
        sunriseAnimation.playAnimation()
    }

    private fun handleSunsetAnimation(){
        val startFrame = 1200
        val stopFrame = 2100
        sunsetAnimation.setMinAndMaxFrame(startFrame, stopFrame)
        sunsetAnimation.speed = 2f
        sunsetAnimation.playAnimation()
    }


}
