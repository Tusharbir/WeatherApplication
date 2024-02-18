package tech.cseb.weatherapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import tech.cseb.weatherapp.fragments.CityOneFragment
import tech.cseb.weatherapp.fragments.HomeFragment

class AdapterFragmentPage(
    fragmentManager: FragmentManager,
    lifecycle: androidx.lifecycle.Lifecycle,
    private val cityLatLongs: List<String>
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return cityLatLongs.size+1
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            else -> {

                val cityIndex = position - 1
                when {
                    cityLatLongs.size > cityIndex -> {

                        CityOneFragment.newInstance(cityLatLongs[cityIndex])
                    }
                    else -> Fragment()
                }
            }
        }
    }
}