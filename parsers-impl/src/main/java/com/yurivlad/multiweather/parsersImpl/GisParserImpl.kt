package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.*
import org.jsoup.Jsoup
import java.util.*

/**
 * for geo search https://www.gismeteo.com/api/v2/search/nearesttownsbycoords/?latitude=42.8068111&longitude=132.8728805&limit=1
 *
 *for weather 3/10 days - https://www.gismeteo.com/weather-nakhodka-4879/3-days/
 */
object GisParserImpl : Parser<Gis10DayForecast> {

    override fun parse(inputHtml: String): Gis10DayForecast {

        val document = Jsoup.parse(inputHtml)
        val forecastScroller =
            document.select(".body > section > div.content_wrap > div > div.main > div > div.__frame_sm > div.forecast_frame.dw_wrap.js_widget > div > div.forecast_scroll > div")

        val forecastTempLine =
            forecastScroller.select(".templine .values .value .unit_temperature_c")
        val forecastWindLine = forecastScroller.select(".windline .w_wind .unit_wind_m_s")
        val forecastIconLine = forecastScroller.select(".iconline .weather_item .img .tooltip")

        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val daysCount = forecastTempLine.size / 4

        fun createForeCastForIndex(index: Int, dayPart: Gis10DayForecastDayPart) =
            Gis10DayForecastPartOfDayItem(
                dayPart,
                forecastIconLine[index].attr("data-text").replace(
                    Regex("(\n)|(</?nobr>)"),
                    ""
                ).trim(),
                forecastTempLine[index].text().replace("−", "-").toInt(),
                forecastWindLine[index].text().toInt()
            )


        val result =
            Gis10DayForecast(
                calendar.time,
                Calendar.getInstance(TimeZone.getDefault()).apply {
                    set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + daysCount - 1)
                }.time,
                (0 until daysCount)
                    .map { dayIndex ->
                        Gis10DayForecastDay(
                            Calendar.getInstance(TimeZone.getDefault()).apply {
                                set(
                                    Calendar.DAY_OF_YEAR,
                                    calendar.get(Calendar.DAY_OF_YEAR) + dayIndex
                                )
                            }.time,
                            nightForecast = createForeCastForIndex(
                                dayIndex * 4,
                                Gis10DayForecastDayPart.NIGHT
                            ),
                            morningForecast = createForeCastForIndex(
                                1 + dayIndex * 4,
                                Gis10DayForecastDayPart.MORNING
                            ),
                            dayForecast = createForeCastForIndex(
                                2 + dayIndex * 4,
                                Gis10DayForecastDayPart.DAY
                            ),
                            eveningForecast = createForeCastForIndex(
                                3 + dayIndex * 4,
                                Gis10DayForecastDayPart.EVENING
                            )
                        )
                    }
            )

        return result
    }
}