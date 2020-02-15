@file:JvmName("WeeklyForecastBindingUtils")

package com.yurivlad.multiweather.weeklyForecastImpl

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yurivlad.multiweather.presenterModel.ForecastForDayPartColumn
import com.yurivlad.multiweather.presenterModel.ForecastWithThreeSourcesPresenterModel
import com.yurivlad.multiweather.presenterUtils.VerticalText
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 */

@BindingAdapter("temperature")
fun TextView.createTempLabel(dayPart: ForecastForDayPartColumn?) {
    text = dayPart?.let {
        resources.getString(R.string.temperature, it.temperature)
    } ?: ""
    visibility = if (dayPart == null) View.GONE else View.VISIBLE
}

@BindingAdapter("wind")
fun TextView.createWindLabel(dayPart: ForecastForDayPartColumn?) {
    text = dayPart?.let {
        resources.getString(R.string.wind, it.windMetersPerSecond)
    } ?: ""
    visibility = if (dayPart == null) View.GONE else View.VISIBLE
}


fun createDateLabelText(forDate: Date): String {
    val sdf = SimpleDateFormat("dd MMMM, EEEE", Locale.getDefault())
    return sdf.format(forDate)
}


@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.refreshing(isRefreshing: Boolean?) {
        postDelayed( {
            this.isRefreshing = isRefreshing == true
        }, 30)
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


@BindingAdapter("imageSrc")
fun ImageView.setImageSrc(@DrawableRes id: Int) {
    if (id == 0) setImageDrawable(null)
    else setImageDrawable(ContextCompat.getDrawable(context, id))
}

@BindingAdapter("vertText")
fun VerticalText.setText(text: String) {
    this.text = text
}

@BindingAdapter("colorScheme", requireAll = false)
fun SwipeRefreshLayout.colorScheme(dummy: String?) {
    setColorSchemeResources(
        R.color.colorDay,
        R.color.colorEvening,
        R.color.colorNight
    )
}
