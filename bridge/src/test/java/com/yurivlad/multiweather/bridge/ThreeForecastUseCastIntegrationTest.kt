package com.yurivlad.multiweather.bridge

import com.yurivlad.multiweather.core.AssignableDispatcher
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import com.yurivlad.multiweather.domainPresenterMappersModel.DayOfMonthMapperParam
import com.yurivlad.multiweather.domainPresenterMappersModel.WeatherWidgetMapper
import com.yurivlad.multiweather.persistence_model.DatabaseDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
class ThreeForecastUseCastIntegrationTest : KoinTest {

    @Test
    fun useCaseTest() = runBlockingTest {
        startKoin {
            loadKoinModules(listOf(appCoreModules, testModule))
        }
        val useCase = get<UseCase<ForecastSources, NoParamsRequest>>()

        useCase.action(NoParamsRequest)

        val forecasts = useCase.resultChannel.valueOrNull.orEmpty()

        Assert.assertEquals(
            "$useCase channel progress lost",
            false,
            useCase.resultChannel.progressOrNull
        )

        Assert.assertEquals(
            3,
            forecasts.size
        )
        Assert.assertTrue(
            forecasts[0].isNotEmpty()
        )
        Assert.assertTrue(
            forecasts[1].isNotEmpty()
        )
        Assert.assertTrue(
            forecasts[2].isNotEmpty()
        )
    }

    @Test
    fun test1() = runBlockingTest {
        startKoin {
            loadKoinModules(listOf(appCoreModules, testModule))
        }

        val currentDay =
            Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"))[Calendar.DAY_OF_MONTH]

        val useCase = get<UseCase<ForecastSources, NoParamsRequest>>()

        useCase.action(NoParamsRequest)

        val forecasts = useCase.resultChannel.valueOrNull.orEmpty()

        val forecast = useCase.resultChannel.openValueSubscription().receive()
        val mapper: WeatherWidgetMapper = get()
        val model = mapper.convert(forecast, DayOfMonthMapperParam(currentDay))

        println(model)
    }
}

@ExperimentalCoroutinesApi
val testModule = module {
    single<DispatchersProvider> { AssignableDispatcher(TestCoroutineDispatcher()) }
    single<CoroutineDispatcher> { TestCoroutineDispatcher() }
    single { createTestDispatcher() }
    single<DatabaseDomain<ForecastWithDayParts, ForecastSource>> {
        object : DatabaseDomain<ForecastWithDayParts, ForecastSource> {
            override suspend fun put(item: ForecastWithDayParts) {

            }

            override suspend fun get(id: ForecastSource): ForecastWithDayParts? {
                return null
            }

            override suspend fun remove(id: ForecastSource) {

            }
        }
    }
}


