package tech.cseb.weatherapp.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import tech.cseb.weatherapp.AppViewModel
import tech.cseb.weatherapp.R
import tech.cseb.weatherapp.adapters.AdapterHourlyWeather
import tech.cseb.weatherapp.adapters.AdapterWeeklyForecast
import tech.cseb.weatherapp.databinding.FragmentSearchedCityBinding
import tech.cseb.weatherapp.search.SearchCityItem
import tech.cseb.weatherapp.utility.loading_utils.LoadingUtils

class SearchedCityFragment : Fragment() {

    private lateinit var binding: FragmentSearchedCityBinding

    private lateinit var cityData: SearchCityItem

    private val viewModel = AppViewModel()
    private lateinit var sunriseAnimation : LottieAnimationView
    private lateinit var sunsetAnimation : LottieAnimationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LoadingUtils.showDialog(requireContext(), false)

        binding = FragmentSearchedCityBinding.inflate(layoutInflater,container,false)

        sunriseAnimation = binding.animSunrise
        sunsetAnimation = binding.animSunset

        binding.ivBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container, RootBlankFragment()).commit()
        }


        cityData = arguments?.getSerializable("cityData") as SearchCityItem

        binding.tvCityName.text = cityData.name

        val query = "${cityData.lat}, ${cityData.lon}"

        this.context?.let { fetchAndSetData(it.applicationContext, query) }


        return binding.root
    }


    private fun fetchAndSetData(context: Context, query: String){
        viewModel.fetchWeatherByLatLong(context,query){
            weatherData, hourlyData, weekData ->

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



            val hourlyAdapter = AdapterHourlyWeather()
            hourlyAdapter.updateHourlyData(hourlyData)
            binding.recyclerHourly.layoutManager = LinearLayoutManager( this.context?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerHourly.adapter = hourlyAdapter



            val weeklyAdapter = AdapterWeeklyForecast()
            weeklyAdapter.updateWeeklyData(weekData)

            binding.recyclerWeekly.layoutManager = LinearLayoutManager(this.context?.applicationContext)
            binding.recyclerWeekly.adapter = weeklyAdapter


            Handler(Looper.getMainLooper()).postDelayed({
                LoadingUtils.hideDialog()
            }, 1000)

            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvDegrees.visibility = View.VISIBLE
                binding.tvCityName.visibility = View.VISIBLE
                binding.tvDate.visibility = View.VISIBLE
                binding.ivWeatherImage.visibility = View.VISIBLE

                binding.tvWeatherStatus.visibility= View.VISIBLE
                binding.cvStats.visibility = View.VISIBLE
                binding.cvAirQuality.visibility = View.VISIBLE
                binding.cvUv.visibility = View.VISIBLE
                binding.cvHourlyForecast.visibility = View.VISIBLE
                binding.cvSevenDayForecast.visibility = View.VISIBLE
                binding.ivWeatherImage.visibility = View.VISIBLE
                binding.cvSunrise.visibility=View.VISIBLE
                binding.cvSunset.visibility=View.VISIBLE

                binding.tvSunrise.text =hourlyData[0].sunrise
                binding.tvSunset.text =hourlyData[0].sunset

                binding.tvDegrees.startAnimation(topAnimation)
                binding.tvCityName.startAnimation(topAnimation)
                binding.tvDate.startAnimation(topAnimation)
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
            },700)
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