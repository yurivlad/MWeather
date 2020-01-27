package com.yurivlad.multiweather.presenterModel

import androidx.annotation.DrawableRes
import java.util.*

/**
 *
 */
data class ForecastWithThreeSourcesPresenterModel(
    val sources: ForecastSourceNames,
    val forecastRows: List<WeeklyForecastRow>
) : PresenterModel, List<WeeklyForecastRow> by forecastRows

data class ForecastSourceNames(val first: String?, val second: String?, val third: String?)

sealed class WeeklyForecastRow {
    abstract val id: String
}

data class DateRow(override val id: String, val date: Date) : WeeklyForecastRow()

data class DayPartRow(
    override val id: String,
    val dayPart: String,
    val firstColumn: ForecastForDayPartColumn?,
    val secondColumn: ForecastForDayPartColumn?,
    val thirdColumn: ForecastForDayPartColumn?
) : WeeklyForecastRow()

data class ForecastForDayPartColumn(
    val summary: String,
    val temperature: String,
    val windMetersPerSecond: String,
    @DrawableRes val weatherPicture: Int
)