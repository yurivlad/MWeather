package com.yurivlad.multiweather.weeklyForecastImpl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.presenterModel.DateRow
import com.yurivlad.multiweather.presenterModel.DayPartRow
import com.yurivlad.multiweather.presenterModel.WeeklyForecastRow
import com.yurivlad.multiweather.presenterCore.StringsProvider
import com.yurivlad.multiweather.weeklyForecastImpl.databinding.VhWeeklyForecastDateRowBinding
import com.yurivlad.multiweather.weeklyForecastImpl.databinding.VhWeeklyForecastRowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import trikita.log.Log


/**
 *
 */
class WeeklyForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {
    private val stringsProvider: StringsProvider by inject()
    private val dispatchersProvider: DispatchersProvider by inject()
    private val workerScope = CoroutineScope(dispatchersProvider.workerDispatcher)
    private val mainScope = CoroutineScope(dispatchersProvider.mainDispatcher)
    private var lastListUpdateJob: Job? = null

    var items: List<WeeklyForecastRow> = emptyList()
        set(value) {
            lastListUpdateJob?.cancel()
            lastListUpdateJob = mainScope.launch {
                val result = workerScope.async {
                    DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                        override fun areItemsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val old = field[oldItemPosition]
                            val new = value[newItemPosition]

                            return old.id == new.id
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
                    })
                }.await()
                result.dispatchUpdatesTo(this@WeeklyForecastAdapter)
                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.vh_weekly_forecast_date_row -> DateRowHolder(parent)
            R.layout.vh_weekly_forecast_row -> {
                val start = System.currentTimeMillis()
                val rh = ForecastRowHolder(parent).apply {
                    binding.stringsProvider = stringsProvider
                }
                Log.d("row inflating took ${System.currentTimeMillis() - start}")
                rh
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