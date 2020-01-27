package com.yurivlad.multiweather.parsersImpl

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GisParsingTest {

    @Test
    fun testGisParsing() {
        val result = GisParserImpl.parse(forecast)
        Assert.assertTrue(
            "forecast list is empty, error parsing forecasts list",
            result.foreCasts.isNotEmpty()
        )
        Assert.assertTrue(
            "error parsing weather items, change in list size",
            result.foreCasts.size == 10
        )
        result.foreCasts.forEach {
            Assert.assertTrue(it.dayForecast.summary.isNotEmpty())
            Assert.assertTrue(it.eveningForecast.summary.isNotEmpty())
            Assert.assertTrue(it.morningForecast.summary.isNotEmpty())
            Assert.assertTrue(it.nightForecast.summary.isNotEmpty())
        }
        Assert.assertEquals("forecast first day is 9 of January",9, Calendar.getInstance().apply {
            time = result.foreCasts.first().date
        }.get(Calendar.DAY_OF_MONTH))
        Assert.assertEquals("forecast last day is 18 of January",18, Calendar.getInstance().apply {
            time = result.foreCasts.last().date
        }.get(Calendar.DAY_OF_MONTH))

        println(result)
    }


    val forecast =
        (GisParsingTest::class.java).getResource("/gis_10_day_forecast_2.html")!!.readText()
}


