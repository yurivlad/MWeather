package com.yurivlad.multiweather.domainPresenterMappersImpl

import android.annotation.SuppressLint
import com.yurivlad.multiweather.core.StringsProvider
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.domainPresenterMappersModel.ToPresenterMapper
import com.yurivlad.multiweather.presenterModel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

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
                val out = ArrayList<ForecastRow>(size * 4)
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
                if (forecast.temperature.from != forecast.temperature.to)
                    stringsProvider.getString(
                        R.string.range_temp,
                        forecast.temperature.from,
                        forecast.temperature.to
                    ) else forecast.temperature.from.toString(),
                if (forecast.windMetersPerSecond.from != forecast.windMetersPerSecond.to)
                    stringsProvider.getString(
                        R.string.range_wind,
                        forecast.windMetersPerSecond.from.formatDouble(),
                        forecast.windMetersPerSecond.to.formatDouble()
                    ) else forecast.windMetersPerSecond.from.formatDouble()
            )

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

