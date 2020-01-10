package com.yurivlad.multiweather.parsersModel

import java.util.*

/**
 *
 */
data class Ya10DayForecast(
    val from: Date,
    val to: Date,
     val foreCasts: List<Ya10DayForecastDay>)

data class Ya10DayForecastPartOfDayItem(
    val dayPart: Ya10DayForecastDayPart,
    val summary: String,
    val temperature: Ya10DayTemperature,
    val windMetersPerSecond: Double
)

data class Ya10DayTemperature(val from:Int, val to: Int)

enum class Ya10DayForecastDayPart {
    NIGHT, MORNING, DAY, EVENING
}

data class Ya10DayForecastDay(
    val date: Date,
    val nightForecast: Ya10DayForecastPartOfDayItem,
    val morningForecast: Ya10DayForecastPartOfDayItem,
    val dayForecast: Ya10DayForecastPartOfDayItem,
    val eveningForecast: Ya10DayForecastPartOfDayItem
)