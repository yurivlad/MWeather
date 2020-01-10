package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.*
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */
//example url https://yandex.ru/pogoda/segment/details?lat=42.824037&lon=132.892811
object YaParserImpl : Parser<Ya10DayForecast> {
    override fun parse(inputHtml: String): Ya10DayForecast {
        val doc = Jsoup.parse(inputHtml)
        val cards = doc.select("div.card").run {
            removeAt(2)
            removeAt(5)
            this
        }

        val sdf = SimpleDateFormat("dd MMMM", Locale("ru"))

        fun createDayPart(
            index: Int,
            summaries: List<String>,
            temperature: List<List<Int>>,
            windMetersPerSecond: List<Double>
        ): Ya10DayForecastPartOfDayItem {
            return Ya10DayForecastPartOfDayItem(
                when (index) {
                    0 -> Ya10DayForecastDayPart.MORNING
                    1 -> Ya10DayForecastDayPart.DAY
                    2 -> Ya10DayForecastDayPart.EVENING
                    3 -> Ya10DayForecastDayPart.NIGHT
                    else -> throw IllegalArgumentException("wrong $index index")
                },
                summaries[index],
                Ya10DayTemperature(temperature[index].first(), temperature[index].last()),
                windMetersPerSecond[index]
            )
        }

        val dayForecasts = cards.map { card ->
            val temps = card.select(".weather-table__temp")
                .map { element ->
                    element.select(".temp__value").map {
                        it.text().replace("−", "-")
                            .toInt()
                    }
                }
            val summaries =
                card.select("td.weather-table__body-cell.weather-table__body-cell_type_condition")
                    .map { it.text() }
            val winds = card.select(".wind-speed").map { it.text().replace(",", ".").toDouble() }
            val dateTitle = card.select("dt[data-anchor]")
            val dayNum = dateTitle.select("strong").text().toInt()
            val month = dateTitle.select("small, span").first().child(0).text()
            val date = "$dayNum $month"


            Ya10DayForecastDay(
                sdf.parse(date),
                createDayPart(3, summaries, temps, winds),
                createDayPart(0, summaries, temps, winds),
                createDayPart(1, summaries, temps, winds),
                createDayPart(2, summaries, temps, winds)
            )
        }
        return Ya10DayForecast(dayForecasts.first().date, dayForecasts.last().date, dayForecasts)
    }
}