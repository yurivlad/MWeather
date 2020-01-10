package com.yurivlad.multiweather.domainModel.model

import com.yurivlad.multiweather.domainModel.DomainModel
import java.util.*

/**
 *
 */
data class ForecastWithDayParts(
    val source: ForecastSource,
    val from: Date,
    val to: Date,
    val forecasts: List<ForecastForDayWithDayParts>
) : DomainModel

data class ForecastForDayWithDayParts(
    val date: Date,
    val nightForecast: ForecastForDay,
    val morningForecast: ForecastForDay,
    val dayForecast: ForecastForDay,
    val eveningForecast: ForecastForDay
) : DomainModel


data class ForecastForDay(
    val dayPart: DayPart,
    val summary: String,
    val temperature: ForecastTemperature,
    val windMetersPerSecond: ForecastWind
) : DomainModel

data class ForecastTemperature(val from: Int, val to: Int)

data class ForecastWind(val from: Double, val to: Double)

enum class DayPart : DomainModel {
    NIGHT, MORNING, DAY, EVENING
}