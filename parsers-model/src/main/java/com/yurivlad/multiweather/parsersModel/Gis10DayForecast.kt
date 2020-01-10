package com.yurivlad.multiweather.parsersModel

import java.util.*

/**
 *
 */
data class Gis10DayForecast(
    val from: Date,
    val to: Date,
     val foreCasts: List<Gis10DayForecastDay>)

data class Gis10DayForecastPartOfDayItem(
    val dayPart: Gis10DayForecastDayPart,
    val summary: String,
    val temperature: Int,
    val windMetersPerSecond: Int
)

enum class Gis10DayForecastDayPart {
    NIGHT, MORNING, DAY, EVENING
}

data class Gis10DayForecastDay(
    val date: Date,
    val nightForecast: Gis10DayForecastPartOfDayItem,
    val morningForecast: Gis10DayForecastPartOfDayItem,
    val dayForecast: Gis10DayForecastPartOfDayItem,
    val eveningForecast: Gis10DayForecastPartOfDayItem
)