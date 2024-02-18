package tech.cseb.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.cseb.weatherapp.databinding.ItemHourlyBinding
import tech.cseb.weatherapp.forecast.HourlyDataModel

class AdapterHourlyWeather:RecyclerView.Adapter<AdapterHourlyWeather.ViewHolder>() {

    private val hourlyDataList = arrayListOf<HourlyDataModel>()
    class ViewHolder(val binding: ItemHourlyBinding): RecyclerView.ViewHolder(binding.root){
            fun onBind(data: HourlyDataModel)
            {
                binding.tvItemHourlyHour.text = data.time
                Glide.with(binding.ivItemHourlyImage.context).load(data.imageUrl).into(binding.ivItemHourlyImage)
                binding.tvItemHourlyTemp.text = data.temp
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHourlyBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    fun updateHourlyData(hourlyDataList : List<HourlyDataModel>){
        this.hourlyDataList.clear()
        this.hourlyDataList.addAll(hourlyDataList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return hourlyDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = hourlyDataList[position]
        holder.onBind(data)
    }
}