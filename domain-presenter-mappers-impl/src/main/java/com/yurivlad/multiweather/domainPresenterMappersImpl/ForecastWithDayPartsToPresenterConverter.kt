package com.yurivlad.multiweather.domainPresenterMappersImpl

import android.annotation.SuppressLint
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.domainPresenterMappersModel.ToPresenterMapper
import com.yurivlad.multiweather.presenterModel.*
import com.yurivlad.multiweather.presenterUtils.StringsProvider
import com.yurivlad.multiweather.sharedResources.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.math.ceil

/**
 *
 */
@ExperimentalCoroutinesApi
class ForecastWithDayPartsToPresenterConverter(private val stringsProvider: StringsProvider) :
    ToPresenterMapper<ForecastSources, NoAdditionalData, ForecastWithThreeSourcesPresenterModel> {
    override fun convert(
        from: ForecastSources,
        additionalData: NoAdditionalData
    ): ForecastWithThreeSourcesPresenterModel {

        val dayNumToForecasts = createDayNumToForecastsMap(from.list)

        return ForecastWithThreeSourcesPresenterModel(
            ForecastSourceNames(
                from.findSourceAndActIfSuccess(ForecastSource.GISMETEO) {
                    stringsProvider.getString(
                        R.string.gis_name
                    )
                },
                from.findSourceAndActIfSuccess(ForecastSource.PRIMPOGODA) {
                    stringsProvider.getString(
                        R.string.prim_name
                    )
                },
                from.findSourceAndActIfSuccess(ForecastSource.YANDEX) {
                    stringsProvider.getString(
                        R.string.ya_name
                    )
                }
            ),
            dayNumToForecasts.run {
                val out = ArrayList<WeeklyForecastRow>(size * 4)
                forEach { mapEntry ->
                    val cal = Calendar.getInstance(TimeZone.getDefault())
                    cal.set(Calendar.DAY_OF_MONTH, mapEntry.key)

                    out.add(DateRow(cal.time))

                    out.addAll(
                        createDayPartRowsForDay(
                            mapEntry.value.firstOrNull { it.first == ForecastSource.GISMETEO }?.second,
                            mapEntry.value.firstOrNull { it.first == ForecastSource.PRIMPOGODA }?.second,
                            mapEntry.value.firstOrNull { it.first == ForecastSource.YANDEX }?.second
                        )
                    )
                    Unit
                }

                out
            }
        )
    }

    private fun createDayPartRowsForDay(
        first: ForecastForDayWithDayParts?,
        second: ForecastForDayWithDayParts?,
        third: ForecastForDayWithDayParts?
    ): List<DayPartRow> {
        return listOf(
            createDayPartRow(
                DayPart.MORNING,
                first?.morningForecast,
                second?.morningForecast,
                third?.morningForecast
            ),
            createDayPartRow(
                DayPart.DAY,
                first?.dayForecast,
                second?.dayForecast,
                third?.dayForecast
            ),
            createDayPartRow(
                DayPart.EVENING,
                first?.eveningForecast,
                second?.eveningForecast,
                third?.eveningForecast
            ),
            createDayPartRow(
                DayPart.NIGHT,
                first?.nightForecast,
                second?.nightForecast,
                third?.nightForecast
            )
        )
    }

    private fun createDayPartRow(
        dayPart: DayPart,
        first: ForecastForDay?,
        second: ForecastForDay?,
        third: ForecastForDay?
    ): DayPartRow {
        return DayPartRow(
            when (dayPart) {
                DayPart.MORNING -> stringsProvider.getString(R.string.morning)
                DayPart.DAY -> stringsProvider.getString(R.string.day)
                DayPart.EVENING -> stringsProvider.getString(R.string.evening)
                DayPart.NIGHT -> stringsProvider.getString(R.string.night)
            },
            createForecastForDayPart(first),
            createForecastForDayPart(second),
            createForecastForDayPart(third)
        )
    }

    private fun createForecastForDayPart(forecastForDay: ForecastForDay?): ForecastForDayPart? {
        return forecastForDay?.let { forecast ->
            ForecastForDayPart(
                forecast.summary,
                if (forecast.temperature.from != forecast.temperature.to) ceil((forecast.temperature.from + forecast.temperature.to) / 2.0).formatDouble()
                else forecast.temperature.from.toString(),
                if (forecast.windMetersPerSecond.from != forecast.windMetersPerSecond.to)
                    ceil((forecast.windMetersPerSecond.from + forecast.windMetersPerSecond.to) / 2.0).formatDouble()
                else forecast.windMetersPerSecond.from.formatDouble(),
                forecast.weatherList.convertToDrawableRes(forecastForDay.dayPart)
            )
        }
    }

    private fun WeatherList.convertToDrawableRes(dayPart: DayPart): Int {

        return when {
            isEmpty() -> 0
            size == 1 -> {//simple weather
                first().toDrawableRes(dayPart)
            }
            else -> {//complex_weather
                when {
                    contains(WeatherType.WINDY)
                            && (contains(WeatherType.CLOUDY) || contains(WeatherType.MAINLY_CLOUDY)) -> R.drawable.ic_cloudy_wind_linear_40dp
                    contains(WeatherType.STORM) && (contains(WeatherType.RAIN) || contains(
                        WeatherType.HEAVY_RAIN
                    ) || contains(WeatherType.SMALL_RAIN)) -> R.drawable.ic_storm_rain_linear_40dp
                    contains(WeatherType.STORM) && (contains(WeatherType.SNOW) || contains(
                        WeatherType.HEAVY_SNOW
                    ) || contains(WeatherType.SMALL_SNOW)) -> R.drawable.ic_storm_with_snow_linear_40dp
                    contains(WeatherType.CLEAR) && (contains(WeatherType.RAIN)|| contains(WeatherType.SMALL_RAIN)) -> R.drawable.ic_rain_clear_linear_40dp
                    contains(WeatherType.SMALL_SNOW) &&  (contains(WeatherType.CLOUDY) || contains(WeatherType.MAINLY_CLOUDY))-> R.drawable.ic_light_snow_linear_40dp
                    else -> first().toDrawableRes(dayPart)
                }
            }
        }
    }

    private fun WeatherType.toDrawableRes(dayPart: DayPart): Int {
        return when (this) {
            WeatherType.CLOUDY -> if (dayPart == DayPart.NIGHT) R.drawable.ic_cloudy_night_linear_40dp else R.drawable.ic_cloudy_clear_day_linear_40dp
            WeatherType.CLEAR -> if (dayPart == DayPart.NIGHT) R.drawable.ic_clear_night_linear_40dp else R.drawable.ic_clear_linear_40dp
            WeatherType.SNOW_WITH_RAIN -> R.drawable.ic_snow_rain_linear_40dp
            WeatherType.MAINLY_CLOUDY -> if (dayPart == DayPart.NIGHT) R.drawable.ic_mainly_coudy_night_linear_40dp else R.drawable.ic_mainly_cloudy_day_linear_40dp
            WeatherType.SMALL_RAIN -> R.drawable.ic_rain_linear_40dp
            WeatherType.RAIN -> R.drawable.ic_rain_small_linear_40dp
            WeatherType.HEAVY_RAIN -> R.drawable.ic_heavy_rain_linear_40dp
            WeatherType.SNOW -> R.drawable.ic_snow_linear_40dp
            WeatherType.SMALL_SNOW -> R.drawable.ic_light_snow_linear_40dp
            WeatherType.HEAVY_SNOW -> R.drawable.ic_heavy_snow_linear_40dp
            WeatherType.STORM -> R.drawable.ic_storm_linear_40dp
            WeatherType.FOG -> if (dayPart == DayPart.NIGHT) R.drawable.ic_foggy_night_linear_40dp else R.drawable.ic_foggy_day_linear_40dp
            WeatherType.WINDY -> R.drawable.ic_windy_linear_40dp
            WeatherType.UNKNOWN -> 0
        }
    }

    private fun Double.formatDouble(): String =
        if (this % 1 == 0.0) toInt().toString() else toString()

    private fun ForecastSources.findSourceAndActIfSuccess(
        source: ForecastSource,
        action: () -> String
    ): String? {
        return firstOrNull { it.source == source }
            ?.run { action() }
    }

    @SuppressLint("UseSparseArrays")
    private fun createDayNumToForecastsMap(forecasts: List<ForecastWithDayParts>): MutableMap<Int, MutableList<Pair<ForecastSource, ForecastForDayWithDayParts>>> {
        val forecastDayMap =
            LinkedHashMap<Int, MutableList<Pair<ForecastSource, ForecastForDayWithDayParts>>>()

        forecasts
            .forEach { forecastWithDayParts ->
                forecastWithDayParts
                    .forEach { forecastForDay ->
                        putForecastToMap(
                            forecastDayMap,
                            forecastForDay,
                            forecastWithDayParts.source
                        )
                    }
            }

        return forecastDayMap

    }

    private fun putForecastToMap(
        map: MutableMap<Int, MutableList<Pair<ForecastSource, ForecastForDayWithDayParts>>>,
        forecastForDayWithDayParts: ForecastForDayWithDayParts,
        source: ForecastSource
    ) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = forecastForDayWithDayParts.date

        val forecastWithSource =
            map
                .getOrPut(calendar.get(Calendar.DAY_OF_MONTH)) { ArrayList(10) }


        forecastWithSource
            .add(source to forecastForDayWithDayParts)
    }
}

