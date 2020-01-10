package com.yurivlad.multiweather.parsersImpl

import com.yurivlad.multiweather.parsersModel.Ya10DayForecastDayPart
import org.junit.Assert
import org.junit.Test

/**
 *
 */
class Ya10DayParsingTest {

    @Test
    fun testParsing() {
        val result = YaParserImpl.parse(document)
        val firstDayForecast = result.foreCasts.first()

        val morningTemp = firstDayForecast.morningForecast
        Assert.assertEquals(-10, morningTemp.temperature.from)
        Assert.assertEquals(-7, morningTemp.temperature.to)
        Assert.assertEquals(3.8, morningTemp.windMetersPerSecond, 0.0)
        Assert.assertEquals("Ясно", morningTemp.summary)
        Assert.assertEquals(morningTemp.dayPart, Ya10DayForecastDayPart.MORNING)

        val nightTemp = firstDayForecast.nightForecast
        Assert.assertEquals(-10, nightTemp.temperature.from)
        Assert.assertEquals(-8, nightTemp.temperature.to)
        Assert.assertEquals(1.7, nightTemp.windMetersPerSecond, 0.0)
        Assert.assertEquals("Ясно", nightTemp.summary)
        Assert.assertEquals(nightTemp.dayPart, Ya10DayForecastDayPart.NIGHT)
    }


    val document =
        (Ya10DayParsingTest::class.java).getResource("/yandex_forecast_10_day.html").readText()

}