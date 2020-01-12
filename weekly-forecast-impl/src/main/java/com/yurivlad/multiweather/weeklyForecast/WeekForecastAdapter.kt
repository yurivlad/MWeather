package com.yurivlad.multiweather.weeklyForecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yurivlad.multiweather.domainModel.model.ForecastForDayWithDayParts

/**
 *
 */
class WeekForecastAdapter : RecyclerView.Adapter<WeeklyForecastItem>() {
    val items: List<ForecastForDayWithDayParts> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyForecastItem {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: WeeklyForecastItem, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class WeeklyForecastItem(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.v_holder_week_forecast,
        parent,
        false
    )
) {
    val forecast: ForecastForDayWithDayParts? = null
}