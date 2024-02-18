package tech.cseb.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.cseb.weatherapp.databinding.ItemSearchBinding
import tech.cseb.weatherapp.search.SearchCityItem

class AdapterSearchCity(private val onCityClick:(SearchCityItem)-> Unit) : RecyclerView.Adapter<AdapterSearchCity.ViewHolder>(){

    private val searchCityList = arrayListOf<SearchCityItem>()
    class ViewHolder(val binding: ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: SearchCityItem, onCityClick: (SearchCityItem) -> Unit){
            val cityName = "${data.name}, ${data.region}, ${data.country}"
            binding.tvItemSearchCityName.text = cityName
            binding.tvItemSearchCityName.setOnClickListener {
                onCityClick(data)
            }
        }
    }

    fun updateSearchData(searchCityList: List<SearchCityItem>){
        this.searchCityList.clear()
        this.searchCityList.addAll(searchCityList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return searchCityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = searchCityList[position]
        holder.onBind(data, onCityClick)
    }

}