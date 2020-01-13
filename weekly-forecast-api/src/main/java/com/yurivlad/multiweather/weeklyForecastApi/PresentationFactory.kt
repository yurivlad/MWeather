package com.yurivlad.multiweather.weeklyForecastApi

import com.yurivlad.multiweather.weeklyForecastImpl.PresentationImpl
import com.yurivlad.multiweather.weeklyForecastModel.Presentation
import org.koin.dsl.module

/**
 *
 */
val weeklyForecastModule = module {
    single<Presentation> { PresentationImpl() }
}