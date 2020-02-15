package com.yurivlad.multiweather.domainPresenterMappersImpl

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainPresenterMappersModel.DayOfMonthMapperParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 *
 */
@ExperimentalCoroutinesApi
class ToWeatherWidgetModelConverterTest {

    @Test
    fun testConvert() {
        val converter = ForecastWithDayPartsToWeatherWidgetModelMapper()
        val moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val forecastSources = moshi.adapter<ForecastSources>(ForecastSources::class.java).fromJson(
            (ToWeatherWidgetModelConverterTest::class.java).getResource("/forecast_sources.json")!!.readText()
        )

        Assert.assertTrue(
            "`forecast_sources.json` file corrupted or missing",
            !forecastSources!!.isEmpty()
        )

        val result = converter.convert(forecastSources, DayOfMonthMapperParam(1))

        Assert.assertNotNull(result.dayForecast)
        Assert.assertNotNull(result.nighForecast)
        Assert.assertNotNull(result.morningForecast)
        Assert.assertNotNull(result.dayForecast)

        val nightForecast = result.nighForecast!!
        Assert.assertEquals(R.drawable.ic_clear_night_linear_40dp, nightForecast.weatherImageSrc)
        Assert.assertEquals("-7" , nightForecast.temperatureCelsius)
        Assert.assertEquals( "4.1", nightForecast.windMetersPerSecond)
    }
}