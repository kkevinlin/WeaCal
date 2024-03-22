package com.example.weatherapp.feature_weather.presenter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.feature_weather.domain.model.DailyWeather
import com.example.weatherapp.databinding.CardDailyWeatherBinding
import com.example.weatherapp.util.WeatherType
import com.example.weatherapp.util.NetworkResult
import com.example.weatherapp.feature_weather.presenter.adapter.model.DailyWeatherAdapterModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
class DailyWeatherAdapter : RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherHolder>(){
    private var dailyWeather: NetworkResult<DailyWeather> = NetworkResult.Success(DailyWeather())

    inner class DailyWeatherHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = CardDailyWeatherBinding.bind(view)

        fun bind(dailyWeatherAdapterModel: DailyWeatherAdapterModel) = with(binding){
            tvDate.text = dailyWeatherAdapterModel.date
            imWeather.setImageResource(dailyWeatherAdapterModel.weatherDrawable)
            tvTemp.text = dailyWeatherAdapterModel.temp
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyWeatherHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            /* resource = */ R.layout.card_daily_weather,
            /* root = */ parent,
            /* attachToRoot = */ false
        )
        return DailyWeatherHolder(view = view)
    }

    override fun onBindViewHolder(holder: DailyWeatherHolder, position: Int) {
        (dailyWeather as NetworkResult.Success<DailyWeather>).data.daily?.apply {
            val date = this.time[position]
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = sdf.parse(date)
            val dayOfWeek = SimpleDateFormat("E", Locale.getDefault()).format(parsedDate) // Format to get the day of the week (e.g., Mon, Tues, etc.)
            val weatherCode = this.weatherCode[position]
            val drawable = WeatherType.toWeatherType(weatherCode).drawableRes
            val tempMax = this.tempMax[position]
            val tempMin = this.tempMin[position]
            val temp = "H:${tempMax}° L:${tempMin}°"
            holder.bind(
                DailyWeatherAdapterModel(
                    date = dayOfWeek, // Use the day of the week
                    weatherDrawable = drawable,
                    temp = temp
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return when (dailyWeather){
            is NetworkResult.Error -> 0
            is NetworkResult.Success -> {
                (dailyWeather as NetworkResult.Success<DailyWeather>).data.daily?.weatherCode?.size ?: 0
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDailyWeather(dailyWeather: NetworkResult<DailyWeather>?){
        this.dailyWeather = dailyWeather ?: NetworkResult.Error(Exception())
        notifyDataSetChanged()
    }

}