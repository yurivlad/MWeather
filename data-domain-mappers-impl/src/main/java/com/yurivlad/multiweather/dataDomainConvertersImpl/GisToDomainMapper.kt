package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Gis10DayForecastDay
import com.yurivlad.multiweather.parsersModel.Gis10DayForecastDayPart
import com.yurivlad.multiweather.parsersModel.Gis10DayForecastPartOfDayItem
import java.util.*

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
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"))
        cal.time = from.date
        return ForecastForDayWithDayParts(
            "${ForecastSource.GISMETEO.name}:${cal.get(Calendar.DAY_OF_MONTH)}:${cal.get(Calendar.MONTH) + 1}",
            cal.time,
            convertDayPartForecast(from.nightForecast),
            convertDayPartForecast(from.morningForecast),
            convertDayPartForecast(from.dayForecast),
            convertDayPartForecast(from.eveningForecast)
        )
    }

    private fun convertDayPartForecast(from: Gis10DayForecastPartOfDayItem): ForecastForDayPart {
        return ForecastForDayPart(
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