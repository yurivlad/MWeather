package com.yurivlad.multiweather.bridge

import com.yurivlad.weeklyForecast.weeklyForecast.weeklyForecastModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules

/**
 *
 */
@ExperimentalCoroutinesApi
object Bridge : KoinComponent {
    fun onAppCreated() {
        loadKoinModules(listOf(appCoreModules,
            weeklyForecastModule, realAppDependencies))
    }
}