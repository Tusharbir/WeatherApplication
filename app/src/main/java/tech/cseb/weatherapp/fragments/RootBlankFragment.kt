package tech.cseb.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import tech.cseb.weatherapp.adapters.AdapterFragmentPage
import tech.cseb.weatherapp.databinding.FragmentRootBlankBinding
import tech.cseb.weatherapp.utility.SharedPreferences.getCityData1
import tech.cseb.weatherapp.utility.SharedPreferences.getCityData2
import tech.cseb.weatherapp.utility.SharedPreferences.getCityData3
import tech.cseb.weatherapp.utility.SharedPreferences.getCounterData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RootBlankFragment : Fragment() {

    private lateinit var binding: FragmentRootBlankBinding
    private lateinit var adapter: FragmentStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRootBlankBinding.inflate(layoutInflater, container, false)

        val cityData = mutableListOf<String>()
        this.context?.applicationContext?.let { context ->
            val counter = getCounterData(context)
            when (counter) {
                1 -> {

                    getCityData1(context)?.let { cityData.add(it) }
                    getCityData3(context)?.let { cityData.add(it) }
                    getCityData2(context)?.let { cityData.add(it) }
                }

                2 -> {

                    getCityData2(context)?.let { cityData.add(it) }
                    getCityData1(context)?.let { cityData.add(it) }
                    getCityData3(context)?.let { cityData.add(it) }
                }

                3 -> {

                    getCityData3(context)?.let { cityData.add(it) }
                    getCityData2(context)?.let { cityData.add(it) }
                    getCityData1(context)?.let { cityData.add(it) }
                }

                else -> {

                    getCityData1(context)?.let { cityData.add(it) }
                    getCityData2(context)?.let { cityData.add(it) }
                    getCityData3(context)?.let { cityData.add(it) }
                }
            }
        }
        val distinctCityData = cityData.distinct()
        adapter = AdapterFragmentPage(childFragmentManager, lifecycle, distinctCityData)

        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })

        binding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })


        // Inflate the layout for this fragment
        return binding.root
    }
}
