package com.yurivlad.multiweather.bridge

import com.yurivlad.multiweather.core.AssignableDispatcher
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.dsl.module
import org.koin.test.KoinTest

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
class ThreeForecastUseCastIntegrationTest : KoinTest {

    @Test
    fun useCaseTest() = runBlocking {
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
}

@ExperimentalCoroutinesApi
val testModule = module {
    single<DispatchersProvider> { AssignableDispatcher(TestCoroutineDispatcher()) }
    single<CoroutineDispatcher> { TestCoroutineDispatcher() }
    single { createTestDispatcher() }
}


