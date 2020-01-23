package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Gis10DayForecastDay
import com.yurivlad.multiweather.parsersModel.Gis10DayForecastDayPart
import com.yurivlad.multiweather.parsersModel.Gis10DayForecastPartOfDayItem

/**
 *
 */
class GisToDomainMapper(private val weatherTypeParser: ToWeatherTypeMapper) :
    ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts> {

    override fun convert(
        from: Gis10DayForecast,
        additionalData: NoAdditionalParams
    ): ForecastWithDayParts {
        return ForecastWithDayParts(
            ForecastSource.GISMETEO,
            from.from, from.to,
            from.foreCasts.map { convertDayForecast(it) }
        )
    }

    private fun convertDayForecast(from: Gis10DayForecastDay): ForecastForDayWithDayParts {
        return ForecastForDayWithDayParts(
            from.date,
            convertDayPartForecast(from.nightForecast),
            convertDayPartForecast(from.morningForecast),
            convertDayPartForecast(from.dayForecast),
            convertDayPartForecast(from.eveningForecast)
        )
    }

    private fun convertDayPartForecast(from: Gis10DayForecastPartOfDayItem): ForecastForDay {
        return ForecastForDay(
            convertDayPart(from.dayPart),
            from.summary,
            ForecastTemperature(from.temperature, from.temperature),
            ForecastWind(from.windMetersPerSecond.toDouble(), from.windMetersPerSecond.toDouble()),
            weatherTypeParser.convert(from.summary, NoAdditionalParams)
        )
    }

    private fun convertDayPart(from: Gis10DayForecastDayPart): DayPart {
        return when (from) {
            Gis10DayForecastDayPart.NIGHT -> DayPart.NIGHT
            Gis10DayForecastDayPart.MORNING -> DayPart.MORNING
            Gis10DayForecastDayPart.DAY -> DayPart.DAY
            Gis10DayForecastDayPart.EVENING -> DayPart.EVENING
        }
    }
}