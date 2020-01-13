@file:JvmName("WeeklyForecastBindingUtils")

package com.yurivlad.multiweather.weeklyForecastImpl

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yurivlad.multiweather.core.StringsProvider
import com.yurivlad.multiweather.presenterModel.ForecastForDayPart
import com.yurivlad.multiweather.presenterModel.ForecastWithThreeSourcesPresenterModel
import trikita.log.Log
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 */


fun createForecast(dayPart: ForecastForDayPart?, stringProvider: StringsProvider): String {
    return if (dayPart != null) stringProvider.getString(
        R.string.day_part_forecast,
        dayPart.summary,
        dayPart.temperature,
        dayPart.windMetersPerSecond
    ) else stringProvider.getString(R.string.unknown_weekly_forecast_row)
}


fun createDateLabelText(forDate: Date): String {
    val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
    return sdf.format(forDate)
}


@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.refreshing(isRefreshing: Boolean?) {
    if (this.isRefreshing != isRefreshing)
        post {
            this.isRefreshing = isRefreshing == true
        }
}

@BindingAdapter("items")
fun RecyclerView.setWeeklyForecastToRecycler(
    model: ForecastWithThreeSourcesPresenterModel?
) {
    if (model == null) return
    post {
        if (adapter == null || adapter !is WeeklyForecastAdapter) {
            adapter = WeeklyForecastAdapter()
        }
        (adapter as WeeklyForecastAdapter).items = model
    }
}

@BindingAdapter("firstSource")
fun TextView.firstForecastSource(model: ForecastWithThreeSourcesPresenterModel?) {
    text = model?.sources?.first
}
@BindingAdapter("secondSource")
fun TextView.secondForecastSource(model: ForecastWithThreeSourcesPresenterModel?) {
    text = model?.sources?.second
}
@BindingAdapter("thirdSource")
fun TextView.thirdForecastSource(model: ForecastWithThreeSourcesPresenterModel?) {
    text = model?.sources?.third
}
