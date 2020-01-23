package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.*
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

/**
 *example url https://m.primpogoda.ru/weather/nahodka/.week
 */

object PrimParserImpl : Parser<Prim7DayForecast> {
    private val nonNumbersRegexp = Regex("\\W")
    private val tempRegex = Regex("[\\n°]")

    override fun parse(inputHtml: String): Prim7DayForecast {

        val sdf = SimpleDateFormat("dd MMMM", Locale("ru"))
        val forecastsPanel = Jsoup.parse(inputHtml).select(".forecast")

        val titlesWithDates = forecastsPanel.select("h3").map { it.text() }

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
            sdf.parse(titlesWithDates.first()),
            sdf.parse(titlesWithDates.last()),//body > div.off-canvas-wrap > div > div.row > div.large-9.medium-9.small-12.columns > div.forecast > div:nth-child(5) > table > tbody > tr.wind.divider.tip-right > td:nth-child(2) > div > div:nth-child(1) > br
            forecastsContainer.mapIndexed { index, forecastContainer ->
                val temperatures =
                    forecastContainer
                        .select(".temperature .hide-for-small")
                        .map {
                            it
                                .text()
                                .replace(tempRegex, "")
                                .split("...")
                                .map { it.toInt() }
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
                                    .filter { Character.isDigit(it) }
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
                    sdf.parse(titlesWithDates[index]),
                    dayParts[0], dayParts[1], dayParts[2], dayParts[3]
                )
            }
        )


    }
}