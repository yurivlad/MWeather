package com.yurivlad.multiweather.weeklyForecastModel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 *
 */
interface Presentation {
    fun getRootFragment(activity: AppCompatActivity): Fragment
}

interface StatefulViewModel {
    fun onComponentStart(bundle: Bundle?)
    fun onSaveState(bundle: Bundle)
    fun onComponentStop()
}