package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import com.yurivlad.multiweather.parsersModel.Prim7DayForecastDay
import com.yurivlad.multiweather.parsersModel.Prim7DayForecastDayPart
import com.yurivlad.multiweather.parsersModel.Prim7DayForecastPartOfDayItem

/**
 *
 */
class PrimToDomainMapper(private val weatherTypeParser: ToWeatherTypeMapper) :
    ToDomainMapper<Prim7DayForecast, NoAdditionalParams, ForecastWithDayParts> {

    override fun convert(
        from: Prim7DayForecast,
        additionalData: NoAdditionalParams
    ): ForecastWithDayParts {
        return ForecastWithDayParts(
            ForecastSource.PRIMPOGODA,
            from.from, from.to,
            from.foreCasts.map { convertDayForecast(it) }
        )
    }

    private fun convertDayForecast(from: Prim7DayForecastDay): ForecastForDayWithDayParts {
        return ForecastForDayWithDayParts(
            from.date,
            convertDayPartForecast(from.nightForecast),
            convertDayPartForecast(from.morningForecast),
            convertDayPartForecast(from.dayForecast),
            convertDayPartForecast(from.eveningForecast)
        )
    }

    private fun convertDayPartForecast(from: Prim7DayForecastPartOfDayItem): ForecastForDay {
        return ForecastForDay(
            convertDayPart(from.dayPart),
            from.summary,
            ForecastTemperature(from.temperature.from, from.temperature.to),
            ForecastWind(
                from.windMetersPerSecond.from.toDouble(),
                from.windMetersPerSecond.to.toDouble()
            ),
            weatherTypeParser.convert(from.summary, NoAdditionalParams)
        )
    }

    private fun convertDayPart(from: Prim7DayForecastDayPart): DayPart {
        return when (from) {
            Prim7DayForecastDayPart.NIGHT -> DayPart.NIGHT
            Prim7DayForecastDayPart.MORNING -> DayPart.MORNING
            Prim7DayForecastDayPart.DAY -> DayPart.DAY
            Prim7DayForecastDayPart.EVENING -> DayPart.EVENING
        }
    }
}