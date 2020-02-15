package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import com.yurivlad.multiweather.parsersModel.Ya10DayForecastDay
import com.yurivlad.multiweather.parsersModel.Ya10DayForecastDayPart
import com.yurivlad.multiweather.parsersModel.Ya10DayForecastPartOfDayItem
import java.util.*

/**
 *
 */
class YaToDomainMapper(private val weatherTypeParser: ToWeatherTypeMapper) :
    ToDomainMapper<Ya10DayForecast, NoAdditionalParams, ForecastWithDayParts> {

    override fun convert(
        from: Ya10DayForecast,
        additionalData: NoAdditionalParams
    ): ForecastWithDayParts {
        return ForecastWithDayParts(
            ForecastSource.YANDEX,
            from.from, from.to,
            from.foreCasts.mapIndexed { index, ya10DayForecastDay -> convertDayForecast(ya10DayForecastDay, from.foreCasts.getOrNull(index - 1)) }
        )
    }

    private fun convertDayForecast(
        from: Ya10DayForecastDay,
        previous: Ya10DayForecastDay?
    ): ForecastForDayWithDayParts {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"))
        cal.time = from.date

        return ForecastForDayWithDayParts(
            "${ForecastSource.YANDEX.name}:${cal.get(Calendar.DAY_OF_MONTH)}:${cal.get(Calendar.MONTH) + 1}",
            cal.time,
            convertDayPartForecast(previous?.nightForecast),
            convertDayPartForecast(from.morningForecast),
            convertDayPartForecast(from.dayForecast),
            convertDayPartForecast(from.eveningForecast)
        )
    }

    private fun convertDayPartForecast(from: Ya10DayForecastPartOfDayItem?): ForecastForDayPart? {
        return from?.let {
            ForecastForDayPart(
                convertDayPart(from.dayPart),
                from.summary,
                ForecastTemperature(from.temperature.from, from.temperature.to),
                ForecastWind(from.windMetersPerSecond, from.windMetersPerSecond),
                weatherTypeParser.convert(from.summary, NoAdditionalParams)
            )
        }
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