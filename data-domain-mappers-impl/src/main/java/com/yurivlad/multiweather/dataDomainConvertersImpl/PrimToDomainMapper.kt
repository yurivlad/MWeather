package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import com.yurivlad.multiweather.parsersModel.Prim7DayForecastDay
import com.yurivlad.multiweather.parsersModel.Prim7DayForecastDayPart
import com.yurivlad.multiweather.parsersModel.Prim7DayForecastPartOfDayItem
import java.util.*

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
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"))
        cal.time = from.date

        return ForecastForDayWithDayParts(
            "${ForecastSource.PRIMPOGODA.name}:${cal.get(Calendar.DAY_OF_MONTH)}:${cal.get(Calendar.MONTH) + 1}",
            cal.time,
            convertDayPartForecast(from.nightForecast),
            convertDayPartForecast(from.morningForecast),
            convertDayPartForecast(from.dayForecast),
            convertDayPartForecast(from.eveningForecast)
        )
    }

    private fun convertDayPartForecast(from: Prim7DayForecastPartOfDayItem): ForecastForDayPart {
        return ForecastForDayPart(
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