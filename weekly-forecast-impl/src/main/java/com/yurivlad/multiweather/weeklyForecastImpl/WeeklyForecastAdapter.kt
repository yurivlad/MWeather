package com.yurivlad.multiweather.weeklyForecastImpl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yurivlad.multiweather.core.StringsProvider
import com.yurivlad.multiweather.presenterModel.DateRow
import com.yurivlad.multiweather.presenterModel.DayPartRow
import com.yurivlad.multiweather.presenterModel.WeeklyForecastRow
import com.yurivlad.multiweather.weeklyForecastImpl.databinding.VhWeeklyForecastDateRowBinding
import com.yurivlad.multiweather.weeklyForecastImpl.databinding.VhWeeklyForecastRowBinding
import org.koin.core.KoinComponent
import org.koin.core.inject


/**
 *
 */
class WeeklyForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {
    private val stringsProvider: StringsProvider by inject()

    var items: List<WeeklyForecastRow> = emptyList()
        set(value) {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return field[oldItemPosition]::class.java == value[newItemPosition]::class.java
                }

                override fun getOldListSize(): Int {
                    return field.size
                }

                override fun getNewListSize(): Int {
                    return value.size
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return field[oldItemPosition] == value[newItemPosition]
                }
            }).dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.vh_weekly_forecast_date_row -> DateRowHolder(parent)
            R.layout.vh_weekly_forecast_row -> ForecastRowHolder(parent).apply {
                binding.stringsProvider = stringsProvider
            }
            else -> throw java.lang.IllegalArgumentException("unknown viewType $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is DateRow -> R.layout.vh_weekly_forecast_date_row
            is DayPartRow -> R.layout.vh_weekly_forecast_row
            else -> throw IllegalArgumentException("unknown item type $item")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ForecastRowHolder -> {
                holder.binding.forecast = item as DayPartRow
            }
            is DateRowHolder -> holder.binding.dateRow = item as DateRow
        }
    }
}

class ForecastRowHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.vh_weekly_forecast_row,
        parent,
        false
    )
) {
    val binding: VhWeeklyForecastRowBinding = VhWeeklyForecastRowBinding.bind(itemView)
}

class DateRowHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.vh_weekly_forecast_date_row,
        parent,
        false
    )
) {
    val binding: VhWeeklyForecastDateRowBinding = VhWeeklyForecastDateRowBinding.bind(itemView)
}