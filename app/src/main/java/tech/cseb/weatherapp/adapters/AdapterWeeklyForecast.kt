package tech.cseb.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.cseb.weatherapp.databinding.ItemWeeklyDataBinding
import tech.cseb.weatherapp.forecast.WeeklyDataModelClass

class AdapterWeeklyForecast: RecyclerView.Adapter<AdapterWeeklyForecast.ViewHolder>() {
    private val weeklyForecastList = arrayListOf<WeeklyDataModelClass>()

    class ViewHolder(val binding: ItemWeeklyDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: WeeklyDataModelClass){
            binding.tvItemWeeklyDay.text = data.day
            Glide.with(binding.ivItemWeeklyIcon.context).load(data.iconUrl).into(binding.ivItemWeeklyIcon)
            binding.ivItemWeeklyTemp.text = data.maxTemp
            binding.ivItemWeeklyMinTemp.text = data.minTemp
        }

    }

    fun updateWeeklyData(weeklyForecastList: List<WeeklyDataModelClass>){
        this.weeklyForecastList.clear()
        this.weeklyForecastList.addAll(weeklyForecastList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemWeeklyDataBinding.inflate(layoutInflater, parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weeklyForecastList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = weeklyForecastList[position]
        holder.onBind(data)
    }

}