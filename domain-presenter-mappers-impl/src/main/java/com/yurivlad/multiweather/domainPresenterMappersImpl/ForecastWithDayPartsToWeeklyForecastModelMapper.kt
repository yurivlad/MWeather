package com.yurivlad.multiweather.domainPresenterMappersImpl

import android.annotation.SuppressLint
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.domainPresenterMappersModel.WeeklyForecastMapper
import com.yurivlad.multiweather.presenterCore.StringsProvider
import com.yurivlad.multiweather.presenterModel.*
import com.yurivlad.multiweather.sharedResources.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.math.ceil

/**
 *
 */
typealias DayAndMonth = Pair<Int, Int>

@ExperimentalCoroutinesApi
class ForecastWithDayPartsToWeeklyForecastModelMapper(private val stringsProvider: StringsProvider) :
    WeeklyForecastMapper {
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
                    val cal = GregorianCalendar.getInstance(Locale.getDefault())
                    cal.set(Calendar.DAY_OF_MONTH, mapEntry.key.first)
                    cal.set(Calendar.MONTH, mapEntry.key.second)

                    out.add(DateRow("dr:${mapEntry.key}", cal.time))

                    out.addAll(
                        createDayPartRowsForDay(
                            mapEntry.value.firstOrNull { it.first == ForecastSource.GISMETEO }?.second,
                            mapEntry.value.firstOrNull { it.first == ForecastSource.PRIMPOGODA }?.second,
                            mapEntry.value.firstOrNull { it.first == ForecastSource.YANDEX }?.second
                        ) { dayPart -> "dpr:${mapEntry.key}:${dayPart.ordinal}" }
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
        third: ForecastForDayWithDayParts?,
        idCreator: (DayPart) -> String
    ): List<DayPartRow> {
        return listOf(
            createDayPartRow(
                DayPart.NIGHT,
                first?.nightForecast,
                second?.nightForecast,
                third?.nightForecast,
                idCreator
            ),
            createDayPartRow(
                DayPart.MORNING,
                first?.morningForecast,
                second?.morningForecast,
                third?.morningForecast,
                idCreator
            ),
            createDayPartRow(
                DayPart.DAY,
                first?.dayForecast,
                second?.dayForecast,
                third?.dayForecast,
                idCreator
            ),
            createDayPartRow(
                DayPart.EVENING,
                first?.eveningForecast,
                second?.eveningForecast,
                third?.eveningForecast,
                idCreator
            )
        )
    }

    private fun createDayPartRow(
        dayPart: DayPart,
        first: ForecastForDayPart?,
        second: ForecastForDayPart?,
        third: ForecastForDayPart?,
        idCreator: (DayPart) -> String
    ): DayPartRow {
        return DayPartRow(
            idCreator(dayPart),
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

    private fun createForecastForDayPart(forecastForDay: ForecastForDayPart?): ForecastForDayPartColumn? {
        return forecastForDay?.let { forecast ->
            ForecastForDayPartColumn(
                forecast.summary,
                if (forecast.temperature.from != forecast.temperature.to) ceil((forecast.temperature.from + forecast.temperature.to) / 2.0).formatDouble()
                else forecast.temperature.from.toString(),
                forecast.windMetersPerSecond.to.formatDouble(),
                forecast.weatherList.convertToDrawableRes(forecastForDay.dayPart)
            )
        }
    }

    private fun ForecastSources.findSourceAndActIfSuccess(
        source: ForecastSource,
        action: () -> String
    ): String? {
        return firstOrNull { it.source == source }
            ?.run { action() }
    }

    @SuppressLint("UseSparseArrays")
    private fun createDayNumToForecastsMap(forecasts: List<ForecastWithDayParts>): MutableMap<DayAndMonth, MutableList<Pair<ForecastSource, ForecastForDayWithDayParts>>> {
        val forecastDayMap =
            LinkedHashMap<DayAndMonth, MutableList<Pair<ForecastSource, ForecastForDayWithDayParts>>>()

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

        return forecastDayMap.toSortedMap(Comparator<DayAndMonth> { o1, o2 ->
            val first = o1 ?: DayAndMonth(0, 0)
            val second = o2 ?: DayAndMonth(0, 0)
            val firstAsInt = first.second * 100 + first.first
            val secondAsInt = second.second * 100 + second.first
            firstAsInt.compareTo(secondAsInt)
        })

    }

    private fun putForecastToMap(
        map: MutableMap<DayAndMonth, MutableList<Pair<ForecastSource, ForecastForDayWithDayParts>>>,
        forecastForDayWithDayParts: ForecastForDayWithDayParts,
        source: ForecastSource
    ) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = forecastForDayWithDayParts.date

        val forecastWithSource =
            map
                .getOrPut(DayAndMonth(calendar[Calendar.DAY_OF_MONTH], calendar[Calendar.MONTH])) { ArrayList(10) }


        forecastWithSource
            .add(source to forecastForDayWithDayParts)
    }
}

