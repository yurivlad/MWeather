package com.yurivlad.multiweather.domainImpl

import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.core.CompositeReceiveChannel
import com.yurivlad.multiweather.domainModel.RepositoryDomain
import com.yurivlad.multiweather.domainModel.RepositoryRequest
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 *
 */
@ExperimentalCoroutinesApi
class ThreeWeatherSourcesForecastUseCaseTest {

    private fun <R : RepositoryRequest> createRepo(thatReturns: () -> ForecastWithDayParts): RepositoryDomain<ForecastWithDayParts, R> {
        return object : RepositoryDomain<ForecastWithDayParts, R> {
            private val channel = CompositeBroadcastChannel<ForecastWithDayParts>()


            override fun getReceiveChannel(request: R): CompositeReceiveChannel<ForecastWithDayParts> {
                return channel
            }

            override fun requestUpdate(request: R) {
                try {
                    channel.progressSendChannel.offer(true)
                    channel.valueSendChannel.offer(thatReturns())
                    channel.errorSendChannel.offer(null)
                } catch (e: Exception) {
                    channel.errorSendChannel.offer(e)
                } finally {
                    channel.progressSendChannel.offer(false)
                }
            }
        }
    }

    @Test
    fun testReturnedValues() = runBlockingTest {
        val gisForecast = ForecastWithDayParts(ForecastSource.GISMETEO, Date(), Date(), emptyList())
        val primForecast =
            ForecastWithDayParts(ForecastSource.PRIMPOGODA, Date(), Date(), emptyList())
        val yaForecast = ForecastWithDayParts(ForecastSource.YANDEX, Date(), Date(), emptyList())

        val useCase = WeatherSourcesForecastUseCase(
            TestCoroutineDispatcher(),
            createRepo { gisForecast },
            createRepo { yaForecast },
            createRepo { primForecast }
        )

        useCase.action(NoParamsRequest)

        val forecasts = useCase.resultChannel.valueOrNull.orEmpty()

        Assert.assertTrue(
            forecasts.contains(gisForecast)
        )
        Assert.assertTrue(
            forecasts.contains(yaForecast)
        )
        Assert.assertTrue(
            forecasts.contains(primForecast)
        )

        Assert.assertEquals(false, useCase.resultChannel.progressOrNull)
    }

    @Test
    fun testReturnedError() = runBlockingTest {
        val gisForecast = ForecastWithDayParts(ForecastSource.GISMETEO, Date(), Date(), emptyList())
        val primForecast =
            ForecastWithDayParts(ForecastSource.PRIMPOGODA, Date(), Date(), emptyList())

        val useCase = WeatherSourcesForecastUseCase(
            TestCoroutineDispatcher(),
            createRepo { gisForecast },
            createRepo { throw Exception() },
            createRepo { primForecast }
        )

        useCase.action(NoParamsRequest)

        val channel = useCase.resultChannel
        val forecasts = channel.valueOrNull.orEmpty()

        Assert.assertTrue(
            forecasts.contains(gisForecast)
        )
        Assert.assertTrue(
            forecasts.contains(primForecast)
        )

        Assert.assertEquals(
            2,
            forecasts.size
        )


        Assert.assertEquals(false, channel.progressOrNull!!)

        Assert.assertNotNull(channel.errorOrNull!!)
    }
}