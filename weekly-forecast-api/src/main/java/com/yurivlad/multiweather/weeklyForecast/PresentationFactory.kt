package com.yurivlad.weeklyForecast.weeklyForecast

import com.yurivlad.multiweather.weeklyForecast.PresentationImpl
import com.yurivlad.multiweather.weeklyForecast.Presentation
import org.koin.dsl.module

/**
 *
 */
val weeklyForecastModule = module {
    single<Presentation> { PresentationImpl() }
}