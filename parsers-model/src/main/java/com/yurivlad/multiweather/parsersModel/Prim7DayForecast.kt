package com.yurivlad.multiweather.parsersModel

import java.util.*

/**
 *
 */
data class Prim7DayForecast(
    val from: Date,
    val to: Date,
    val foreCasts: List<Prim7DayForecastDay>
)

data class Prim7DayForecastPartOfDayItem(
    val dayPart: Prim7DayForecastDayPart,
    val summary: String,
    val temperature: Prim7DayTemperature,
    val windMetersPerSecond: Prim7DayWind
)

data class Prim7DayTemperature(val from: Int, val to: Int)

data class Prim7DayWind(val from: Int, val to: Int)

enum class Prim7DayForecastDayPart {
    NIGHT, MORNING, DAY, EVENING
}

data class Prim7DayForecastDay(
    val date: Date,
    val nightForecast: Prim7DayForecastPartOfDayItem,
    val morningForecast: Prim7DayForecastPartOfDayItem,
    val dayForecast: Prim7DayForecastPartOfDayItem,
    val eveningForecast: Prim7DayForecastPartOfDayItem
)