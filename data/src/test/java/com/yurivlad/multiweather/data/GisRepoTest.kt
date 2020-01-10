package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.GisApiService
import com.yurivlad.multiweather.core.AssignableDispatcher
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Gis10DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 *
 */
@ExperimentalCoroutinesApi
class GisRepoTest {
    @Test
    fun testEmptyState() = runBlocking {
        val repo = Gis10DayForecastRepositoryImpl(object : GisApiService {

            override suspend fun get10DayForecast(forecastPagePath: String): Gis10DayForecast {
                throw Exception()
            }
        }, object : ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts> {
            override fun convert(
                from: Gis10DayForecast,
                additionalData: NoAdditionalParams
            ): ForecastWithDayParts {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        },
            TestCoroutineDispatcher()
        )
        val testRequest = Gis10DayForecastRequest("")
        val channel = repo.getModel(testRequest)

        Assert.assertNull("empty repository must return null", channel.progressOrNull)
        Assert.assertNull("empty repository must return null", channel.errorOrNull)
        Assert.assertNull("empty repository must return null", channel.valueOrNull)

        repo.requestUpdate(testRequest)

        Assert.assertNull(
            "value must be null, b/c api thrown an error",
            channel.valueOrNull
        )
        Assert.assertEquals(
            "after update request must be false",
            false,
            channel.progressOrNull
        )
        Assert.assertEquals("channel lost value", false, channel.progressOrNull)

        Assert.assertNotNull("thrown error must be held", channel.errorOrNull)

    }

    @Test
    fun testValueFetch() = runBlocking {
        val repo = Gis10DayForecastRepositoryImpl(object : GisApiService {
            override suspend fun get10DayForecast(forecastPagePath: String): Gis10DayForecast =
                emptyGisForecast
        }, object : ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts> {
            override fun convert(
                from: Gis10DayForecast,
                additionalData: NoAdditionalParams
            ): ForecastWithDayParts {
                Assert.assertEquals("", emptyGisForecast, from)
                return emptyForecastModel
            }
        },
            TestCoroutineDispatcher()
        )
        val testRequest = Gis10DayForecastRequest("")
        val channel = repo.getModel(testRequest)
        repo.requestUpdate(testRequest)

        Assert.assertEquals("progress state lost", false, channel.progressOrNull)
        Assert.assertEquals(
            "repo must return empty3DayForecastModel",
            emptyForecastModel,
            channel.valueOrNull
        )
        Assert.assertEquals(
            "channel lost value",
            emptyForecastModel,
            channel.valueOrNull
        )
    }

    @Test
    fun testErrorRecovery() = runBlocking {
        val repo = Gis10DayForecastRepositoryImpl(object : GisApiService {
            override suspend fun get10DayForecast(forecastPagePath: String): Gis10DayForecast =
                emptyGisForecast
        }, object : ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts> {
            var counter = 0
            override fun convert(
                from: Gis10DayForecast,
                additionalData: NoAdditionalParams
            ): ForecastWithDayParts {

                Assert.assertEquals("", emptyGisForecast, from)

                if (counter == 0) {
                    counter++
                    throw java.lang.Exception()
                }
                return emptyForecastModel
            }
        },
            TestCoroutineDispatcher()
        )
        val testRequest = Gis10DayForecastRequest("")
        val channel = repo.getModel(testRequest)
        repo.requestUpdate(testRequest)

        Assert.assertEquals("progress state lost", false, channel.progressOrNull)
        Assert.assertNull(channel.valueOrNull)
        Assert.assertNotNull(channel.errorOrNull)

        repo.requestUpdate(testRequest)

        Assert.assertEquals("progress state lost", false, channel.progressOrNull)
        Assert.assertEquals(
            "repo must return empty3DayForecastModel",
            emptyForecastModel,
            channel.valueOrNull
        )
        Assert.assertEquals(
            "channel lost value",
            emptyForecastModel,
            channel.valueOrNull
        )
        Assert.assertNull(
            "error state wasn't deleted on update success",
            channel.errorOrNull
        )
    }
}

private val emptyGisForecast: Gis10DayForecast = Gis10DayForecast(Date(), Date(), emptyList())

private val emptyForecastModel = ForecastWithDayParts(
    ForecastSource.GISMETEO,
    Date(),
    Date(),
    emptyList()
)
