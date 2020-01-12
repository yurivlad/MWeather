package com.yurivlad.multiweather.presenterModel

import java.util.*

/**
 *
 */
data class ForecastWithThreeSourcesPresenterModel(
    val sources: ForecastSourceNames,
    val forecastRows: List<ForecastRow>
) : PresenterModel, List<ForecastRow> by forecastRows

data class ForecastSourceNames(val first: String?, val second: String?, val third: String?)

sealed class ForecastRow

data class DateRow(val date: Date) : ForecastRow()

data class DayPartRow(
    val dayPart: String,
    val firstColumn: ForecastForDayPart?,
    val secondColumn: ForecastForDayPart?,
    val thirdColumn: ForecastForDayPart?
) : ForecastRow()

data class ForecastForDayPart(
    val summary: String,
    val temperature: String,
    val windMetersPerSecond: String
)