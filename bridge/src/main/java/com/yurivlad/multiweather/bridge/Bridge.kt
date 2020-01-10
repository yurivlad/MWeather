package com.yurivlad.multiweather.bridge

import com.yurivlad.presentationApi.presentationModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules

/**
 *
 */
@ExperimentalCoroutinesApi
object Bridge : KoinComponent {
    fun onAppCreated() {
        loadKoinModules(listOf(appCoreModules, presentationModule, extModule))
    }
}