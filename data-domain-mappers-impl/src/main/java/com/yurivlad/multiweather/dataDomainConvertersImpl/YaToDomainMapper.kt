package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.parsersModel.*

/**
 *
 */
class YaToDomainMapper(private val weatherTypeParser: ToWeatherTypeMapper) : ToDomainMapper<Ya10DayForecast, NoAdditionalParams, ForecastWithDayParts> {

    override fun convert(
        from: Ya10DayForecast,
        additionalData: NoAdditionalParams
    ): ForecastWithDayParts {
        return ForecastWithDayParts(
            ForecastSource.YANDEX,
            from.from, from.to,
            from.foreCasts.map { convertDayForecast(it) }
        )
    }

    private fun convertDayForecast(from: Ya10DayForecastDay): ForecastForDayWithDayParts {
        return ForecastForDayWithDayParts(
            from.date,
            convertDayPartForecast(from.nightForecast),
            convertDayPartForecast(from.morningForecast),
            convertDayPartForecast(from.dayForecast),
            convertDayPartForecast(from.eveningForecast)
        )
    }

    private fun convertDayPartForecast(from: Ya10DayForecastPartOfDayItem): ForecastForDay {
        return ForecastForDay(
            convertDayPart(from.dayPart),
            from.summary,
            ForecastTemperature(from.temperature.from, from.temperature.to),
            ForecastWind(from.windMetersPerSecond, from.windMetersPerSecond),
            weatherTypeParser.convert(from.summary, NoAdditionalParams)
        )
    }

    private fun convertDayPart(from: Ya10DayForecastDayPart): DayPart {
        return when (from) {
            Ya10DayForecastDayPart.NIGHT -> DayPart.NIGHT
            Ya10DayForecastDayPart.MORNING -> DayPart.MORNING
            Ya10DayForecastDayPart.DAY -> DayPart.DAY
            Ya10DayForecastDayPart.EVENING -> DayPart.EVENING
        }
    }
}