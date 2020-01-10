package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.Prim7DayForecastDayPart
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 *
 */
class Prim7DayParsingTest {

    @Before
    fun before() {
        Assert.assertTrue(document.isNotEmpty())
    }

    @Test
    fun testParsing() {
        val result = PrimParserImpl.parse(document)

        val morningForecast = result.foreCasts.first().morningForecast

        Assert.assertEquals("Ясная погода", morningForecast.summary)
        Assert.assertEquals(-8, morningForecast.temperature.from)
        Assert.assertEquals(-10, morningForecast.temperature.to)
        Assert.assertEquals(1, morningForecast.windMetersPerSecond.from)
        Assert.assertEquals(2, morningForecast.windMetersPerSecond.to)
        Assert.assertEquals(Prim7DayForecastDayPart.MORNING, morningForecast.dayPart)


    }

    val document =
        (Prim7DayParsingTest::class.java).getResource("/prim_7_day_forecast.html").readText()
}