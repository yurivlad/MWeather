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
) : DomainModel, List<ForecastForDayWithDayParts> by forecasts

data class ForecastForDayWithDayParts(
    val id: String,
    val date: Date,
    val nightForecast: ForecastForDayPart?,
    val morningForecast: ForecastForDayPart?,
    val dayForecast: ForecastForDayPart?,
    val eveningForecast: ForecastForDayPart?
) : DomainModel


data class ForecastForDayPart(
    val dayPart: DayPart,
    val summary: String,
    val temperature: ForecastTemperature,
    val windMetersPerSecond: ForecastWind,
    val weatherList: WeatherList
) : DomainModel

data class ForecastTemperature(val from: Int, val to: Int)

data class ForecastWind(val from: Double, val to: Double)

enum class DayPart : DomainModel {
    NIGHT, MORNING, DAY, EVENING
}
