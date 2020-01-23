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

sealed class WeeklyForecastRow

data class DateRow(val date: Date) : WeeklyForecastRow()

data class DayPartRow(
    val dayPart: String,
    val firstColumn: ForecastForDayPart?,
    val secondColumn: ForecastForDayPart?,
    val thirdColumn: ForecastForDayPart?
) : WeeklyForecastRow()

data class ForecastForDayPart(
    val summary: String,
    val temperature: String,
    val windMetersPerSecond: String,
    @DrawableRes val weatherPicture: Int
)