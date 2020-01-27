package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.*
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

/**
 *example url https://m.primpogoda.ru/weather/nahodka/.week
 */

object PrimParserImpl : Parser<Prim7DayForecast> {
    private val tempRegex = Regex("[\\n°]")

    override fun parse(inputHtml: String): Prim7DayForecast {

        val sdf = SimpleDateFormat("dd MMMM", Locale("ru"))
        val forecastsPanel = Jsoup.parse(inputHtml).select(".forecast")

        val year = Calendar.getInstance(TimeZone.getTimeZone("GTM+0:00")).get(Calendar.YEAR)

        val forecastDates = forecastsPanel.select("h3").map { it.text() }.map { dateString ->
            Calendar.getInstance()
                .apply {
                    time = sdf.parse(dateString)
                    set(Calendar.YEAR, year)
                }
                .time
        }

        val forecastsContainer = forecastsPanel.select(".table-container")

        fun createDayPartItem(
            index: Int,
            temperatures: List<List<Int>>,
            summaries: List<String>,
            winds: List<List<Int>>
        ) = Prim7DayForecastPartOfDayItem(
            when (index) {
                0 -> Prim7DayForecastDayPart.NIGHT
                1 -> Prim7DayForecastDayPart.MORNING
                2 -> Prim7DayForecastDayPart.DAY
                3 -> Prim7DayForecastDayPart.EVENING
                else -> throw Exception()
            },
            summaries[index],
            Prim7DayTemperature(temperatures[index].first(), temperatures[index].last()),
            Prim7DayWind(winds[index].first(), winds[index].last())

        )



        return Prim7DayForecast(
            forecastDates.first(),
            forecastDates.last(),
            forecastsContainer.mapIndexed { index, forecastContainer ->
                val temperatures =
                    forecastContainer
                        .select(".temperature .hide-for-small")
                        .map {
                            it
                                .text()
                                .replace(tempRegex, "")
                                .split("...")
                                .map { temp -> temp.toInt() }
                        }

                val summaries = forecastContainer.select(".divider.weather td .tip-right")
                    .map {
                        it.attr("title").toString()
                    }
                val winds = forecastContainer
                    .select(".wind td .text-center div:nth-child(1)")
                    .map { element ->
                        element
                            .text()
                            .split("-")
                            .map {
                                it
                                    .filter { windSpeed-> Character.isDigit(windSpeed) }
                                    .toIntOrNull() ?: 0
                            }

                    }

                val dayParts = (0..3).map { dayPartIndex ->
                    createDayPartItem(
                        dayPartIndex,
                        temperatures,
                        summaries,
                        winds
                    )
                }
                Prim7DayForecastDay(
                    forecastDates[index],
                    dayParts[0], dayParts[1], dayParts[2], dayParts[3]
                )
            }
        )


    }
}