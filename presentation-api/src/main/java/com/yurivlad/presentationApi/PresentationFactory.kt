package com.yurivlad.presentationApi

import com.yurivlad.multiweather.presentationImpl.PresentationImpl
import com.yurivlad.multiweather.presentationModel.Presentation
import org.koin.dsl.module

/**
 *
 */
val presentationModule = module {
    single<Presentation> { PresentationImpl() }
}