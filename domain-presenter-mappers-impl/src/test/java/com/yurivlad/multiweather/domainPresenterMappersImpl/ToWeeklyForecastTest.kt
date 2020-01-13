package com.yurivlad.multiweather.domainPresenterMappersImpl

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yurivlad.multiweather.core.StringsProvider
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.presenterModel.DateRow
import com.yurivlad.multiweather.presenterModel.DayPartRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 *
 */
@ExperimentalCoroutinesApi
class ToWeeklyForecastTest {

    @Test
    fun testConvert() {
        val sp = object : StringsProvider {
            override fun getString(resId: Int): String {
                return when (resId) {
                    R.string.morning -> "morning"
                    R.string.day -> "day"
                    R.string.evening -> "evening"
                    R.string.night -> "night"
                    else -> "#test#"
                }
            }

            override fun getString(resId: Int, vararg formatArgs: Any): String {
                return when (resId) {
                    R.string.range_temp -> "${formatArgs[0]}..${formatArgs[1]} °C"
                    R.string.range_wind -> "${formatArgs[0]}..${formatArgs[1]} m/s"
                    else -> "#test#"
                }

            }
        }
        val converter = ForecastWithDayPartsToPresenterConverter(sp)
        val moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val forecastSources = moshi.adapter<ForecastSources>(ForecastSources::class.java).fromJson(
            (ToWeeklyForecastTest::class.java).getResource("/forecast_sources.json")!!.readText()
        )

        Assert.assertTrue(
            "`forecast_sources.json` file corrupted or missing",
            !forecastSources!!.isEmpty()
        )

        val result = converter.convert(forecastSources, NoAdditionalData)

        Assert.assertEquals(
            "size must be 50, 4 per day, 10 days + 10 date rows",
            (4 * 10) + 10,
            result.size
        )

        val dates = result.mapNotNull { it as? DateRow }
        Assert.assertEquals(
            "there must be 10 dates",
            10,
            dates.size
        )

        val calendar = Calendar.getInstance()

        var day = 12
        dates.forEach {
            calendar.time = it.date
            Assert.assertEquals("day order lost", day, calendar.get(Calendar.DAY_OF_MONTH))
            day++
        }


        val forecastRows = result.mapNotNull { it as? DayPartRow }
        val first = forecastRows.first()

        Assert.assertEquals(
            "day order lost",
            sp.getString(R.string.morning),
            forecastRows[0].dayPart
        )
        Assert.assertEquals("day order lost", sp.getString(R.string.day), forecastRows[1].dayPart)
        Assert.assertEquals(
            "day order lost",
            sp.getString(R.string.evening),
            forecastRows[2].dayPart
        )
        Assert.assertEquals("day order lost", sp.getString(R.string.night), forecastRows[3].dayPart)

        Assert.assertEquals("Ясно", first.firstColumn?.summary)
        Assert.assertEquals("Облачно", first.secondColumn?.summary)
        Assert.assertEquals("Ясно", first.thirdColumn?.summary)

        Assert.assertEquals("gis temperature lost", "-9", first.firstColumn?.temperature)
        Assert.assertEquals(
            "prim temperature lost",
            "-8",
            first.secondColumn?.temperature
        )
        Assert.assertEquals("gis wind lost", "4", first.firstColumn?.windMetersPerSecond)
    }
}