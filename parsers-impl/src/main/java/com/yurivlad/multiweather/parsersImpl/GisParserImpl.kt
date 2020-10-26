package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.*
import org.jsoup.Jsoup
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
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

        val sdf = SimpleDateFormat("dd MMM", Locale("ru"))
        val year = Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00")).get(Calendar.YEAR)
        val dates = forecastScroller.select(".clearfix .header_item").map { it.text().substring(4) }
            .map { timeString ->
                (Calendar.getInstance(TimeZone.getDefault())
                    .also {
                        it.time =
                            sdf.parse(timeString.replace("фев", "февр")
                                .replace("мар", "марта")
                                .replace("сен", "сент")
                                .replace("ноя", "ноября")
                            )
                        it.set(Calendar.YEAR, year)
                    }).time
            }

        val daysCount = forecastTempLine.size / 4

        fun createForeCastForIndex(index: Int, dayPart: Gis10DayForecastDayPart) =
            Gis10DayForecastPartOfDayItem(
                dayPart,
                forecastIconLine[index].attr("data-text")
                    .replace(
                        Regex("(\n)|(</?nobr>)"),
                        ""
                    ).replace("&nbsp;", " ")
                    .trim(),
                forecastTempLine[index].text().replace("−", "-").toInt(),
                forecastWindLine[index].text().toInt()
            )


        return Gis10DayForecast(
            dates.first(),
            dates.last(),
            (0 until daysCount)
                .map { dayIndex ->
                    Gis10DayForecastDay(
                        dates[dayIndex],
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
    }
}