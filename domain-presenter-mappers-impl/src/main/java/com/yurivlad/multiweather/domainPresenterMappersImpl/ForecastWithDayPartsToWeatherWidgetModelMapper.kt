package com.yurivlad.multiweather.domainPresenterMappersImpl

import com.yurivlad.multiweather.domainModel.model.DayPart
import com.yurivlad.multiweather.domainModel.model.ForecastForDayPart
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.WeatherList
import com.yurivlad.multiweather.domainPresenterMappersModel.DayOfMonthMapperParam
import com.yurivlad.multiweather.domainPresenterMappersModel.WeatherWidgetMapper
import com.yurivlad.multiweather.presenterModel.WeatherWidgetModel
import com.yurivlad.multiweather.presenterModel.WeatherWidgetPayPart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

/**
 *
 */
@ExperimentalCoroutinesApi
class ForecastWithDayPartsToWeatherWidgetModelMapper : WeatherWidgetMapper {
    override fun convert(
        from: ForecastSources,
        additionalData: DayOfMonthMapperParam
    ): WeatherWidgetModel {

        val requestedDay = additionalData.day
        val calendarForZeroTimeZone = Calendar.getInstance()

        val forecastForCurrentDay = from
            .flatMap { it.forecasts }
            .filter { forecastWithDayParts ->
                calendarForZeroTimeZone.time = forecastWithDayParts.date
                val forecastDay = calendarForZeroTimeZone.get(Calendar.DAY_OF_MONTH)
                forecastDay == requestedDay
            }

        return WeatherWidgetModel(
            createWeatherWidgetPayPart(
                forecastForCurrentDay.mapNotNull { it.nightForecast },
                DayPart.NIGHT
            ),
            createWeatherWidgetPayPart(
                forecastForCurrentDay.mapNotNull { it.morningForecast },
                DayPart.MORNING
            ),
            createWeatherWidgetPayPart(
                forecastForCurrentDay.mapNotNull { it.dayForecast },
                DayPart.DAY
            ),
            createWeatherWidgetPayPart(
                forecastForCurrentDay.mapNotNull { it.eveningForecast },
                DayPart.EVENING
            )
        )
    }

    private fun createWeatherWidgetPayPart(
        from: List<ForecastForDayPart>,
        dayPart: DayPart
    ): WeatherWidgetPayPart? {
        if (from.isEmpty()) return null

        val temperaturesList =
            from.map { listOf(it.temperature.from, it.temperature.to).distinct() }.flatten()
                .sorted()
        val temperaturesListSize = temperaturesList.size

        val windsList =
            from.map { listOf(it.windMetersPerSecond.from, it.windMetersPerSecond.to).distinct() }
                .flatten().sorted()
        val windsListSize = windsList.size

        return WeatherWidgetPayPart(
            convertWeatherOccurrencesToDrawableRes(from, dayPart),
            when {
                temperaturesList.size == 1 -> {
                    temperaturesList[0].toString()
                }
                temperaturesListSize % 2 == 0 -> temperaturesList[temperaturesListSize / 2].toString()
                else -> ((temperaturesList[temperaturesListSize / 2] + temperaturesList[(temperaturesListSize / 2) - 1]) / 2).toString()
            },
            when {
                windsList.size == 1 -> {
                    windsList[0].formatDouble()
                }
                windsListSize % 2 == 0 -> windsList[windsListSize / 2].formatDouble()
                else -> ((windsList[windsListSize / 2] + windsList[(windsListSize / 2) - 1]) / 2).formatDouble()
            }
        )
    }

    private fun convertWeatherOccurrencesToDrawableRes(
        source: List<ForecastForDayPart>,
        dayPart: DayPart
    ): Int {
        if (source.isEmpty()) return 0

        val weatherTypeToItsOccurrence =
            source.asSequence().map { it.weatherList.list }
                .flatten()
                .groupBy { it }
                .map { it.key to it.value.size }
                .sortedBy { it.second }
                .toList()

        val first = weatherTypeToItsOccurrence.first()
        val second = weatherTypeToItsOccurrence.getOrNull(1)

        val weatherList: WeatherList

        weatherList = if (second?.second == first.second) {//if same occurrence
            WeatherList(setOf(first.first, second.first))
        } else {
            WeatherList(setOf(first.first))
        }

        return weatherList.convertToDrawableRes(dayPart)
    }
}

