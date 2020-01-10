package com.yurivlad.multiweather

import android.app.Application
import com.yurivlad.multiweather.bridge.Bridge
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import trikita.log.Log

/**
 *
 */
@ExperimentalCoroutinesApi
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("on App create")
        startKoin {
            androidContext(this@App)
        }
        Bridge.onAppCreated()
    }
}