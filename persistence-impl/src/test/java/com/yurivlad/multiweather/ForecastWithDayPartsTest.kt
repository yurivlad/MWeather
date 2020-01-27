package com.yurivlad.multiweather

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.persistence_impl.DataBaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 *
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@ExperimentalCoroutinesApi
class ForecastWithDayPartsTest : KoinComponent {
    private lateinit var database: DataBaseImpl
    @Before
    fun before() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DataBaseImpl::class.java
        ).allowMainThreadQueries()
            .addMigrations(object : Migration(1, Int.MAX_VALUE) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("drop table ForecastForDayWithDayPartEntity")
                }
            })
            .build()
        if (GlobalContext.getOrNull() == null)
            startKoin {
                modules(module {
                    single { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
                })
            }
    }

    @Test
    fun smokyTest() = runBlockingTest {
        database.put(
            ForecastWithDayParts(
                ForecastSource.GISMETEO, Date(), Date(), listOf(
                    ForecastForDayWithDayParts(
                        UUID.randomUUID().toString(),
                        Date(), null, null, null, null
                    )
                )
            )
        )

        val result = database.get(ForecastSource.GISMETEO)

        Assert.assertNotNull(result)
    }

    @Test
    fun testPut() = runBlockingTest {
        Assert.assertNull(database.get(ForecastSource.PRIMPOGODA))
        Assert.assertNull(database.get(ForecastSource.YANDEX))
        Assert.assertNull(database.get(ForecastSource.GISMETEO))

        val dayForecast = createForecastForDayWithDayParts()

        val forecast = ForecastWithDayParts(
            ForecastSource.GISMETEO, dayForecast.date, dayForecast.date, listOf(dayForecast)
        )
        database.put(forecast)

        var fetchedForecast = database.get(ForecastSource.GISMETEO)!!

        Assert.assertEquals(forecast.from.time, fetchedForecast.from.time)
        Assert.assertEquals(forecast.source, fetchedForecast.source)
        Assert.assertEquals(forecast.to.time, fetchedForecast.to.time)
        Assert.assertEquals(forecast.forecasts, fetchedForecast.forecasts)


        val forecastList = listOf(
            createForecastForDayWithDayParts(),
            createForecastForDayWithDayParts(),
            createForecastForDayWithDayParts()
        )
        val anotherForecast = ForecastWithDayParts(
            ForecastSource.PRIMPOGODA,
            forecastList.first().date,
            forecastList.last().date,
            forecastList
        )
        database.put(anotherForecast)

        fetchedForecast = database.get(ForecastSource.PRIMPOGODA)!!

        Assert.assertEquals(anotherForecast, fetchedForecast)
    }

    @Test
    fun testDelete() = runBlockingTest {
        val dayForecast = createForecastForDayWithDayParts()

        val forecast = ForecastWithDayParts(
            ForecastSource.GISMETEO, dayForecast.date, dayForecast.date, listOf(dayForecast)
        )
        database.put(forecast)

        Assert.assertNotNull(database.get(ForecastSource.GISMETEO))

        database.remove(ForecastSource.GISMETEO)

        Assert.assertNull(database.get(ForecastSource.GISMETEO))

        database.put(
            ForecastWithDayParts(
                ForecastSource.PRIMPOGODA,
                Date(),
                Date(),
                listOf(createForecastForDayWithDayParts())
            )
        )
        database.put(
            ForecastWithDayParts(
                ForecastSource.PRIMPOGODA,
                Date(),
                Date(),
                listOf(createForecastForDayWithDayParts())
            )
        )
        database.put(
            ForecastWithDayParts(
                ForecastSource.GISMETEO,
                Date(),
                Date(),
                listOf(createForecastForDayWithDayParts())
            )
        )
        database.put(
            ForecastWithDayParts(
                ForecastSource.YANDEX,
                Date(),
                Date(),
                listOf(createForecastForDayWithDayParts())
            )
        )

        Assert.assertNotNull(database.get(ForecastSource.PRIMPOGODA))
        Assert.assertNotNull(database.get(ForecastSource.GISMETEO))
        Assert.assertNotNull(database.get(ForecastSource.YANDEX))

        database.remove(ForecastSource.PRIMPOGODA)

        Assert.assertNull(database.get(ForecastSource.PRIMPOGODA))
        Assert.assertNotNull(database.get(ForecastSource.GISMETEO))
        Assert.assertNotNull(database.get(ForecastSource.YANDEX))

        database.remove(ForecastSource.GISMETEO)

        Assert.assertNull(database.get(ForecastSource.PRIMPOGODA))
        Assert.assertNull(database.get(ForecastSource.GISMETEO))
        Assert.assertNotNull(database.get(ForecastSource.YANDEX))

        database.remove(ForecastSource.YANDEX)

        Assert.assertNull(database.get(ForecastSource.PRIMPOGODA))
        Assert.assertNull(database.get(ForecastSource.GISMETEO))
        Assert.assertNull(database.get(ForecastSource.YANDEX))


    }

    private fun createForecastForDayWithDayParts() = ForecastForDayWithDayParts(
        UUID.randomUUID().toString(),
        Date(),
        ForecastForDayPart(
            DayPart.NIGHT, "cvbcb", ForecastTemperature(0, 1), ForecastWind(0.0, 0.1), WeatherList(
                setOf(WeatherType.MAINLY_CLOUDY, WeatherType.CLOUDY)
            )
        ),
        ForecastForDayPart(
            DayPart.MORNING,
            "12412dvcz",
            ForecastTemperature(1, 1),
            ForecastWind(2.0, 0.1),
            WeatherList(
                setOf(WeatherType.CLOUDY, WeatherType.CLOUDY)
            )
        ),
        ForecastForDayPart(
            DayPart.DAY, "13scbsfb", ForecastTemperature(1, 2), ForecastWind(2.4, 0.1), WeatherList(
                setOf(WeatherType.CLOUDY, WeatherType.CLEAR)
            )
        ),
        ForecastForDayPart(
            DayPart.EVENING,
            "123dsdg",
            ForecastTemperature(1, 2),
            ForecastWind(2.4, 0.1),
            WeatherList(
                emptySet()
            )
        )
    )
}