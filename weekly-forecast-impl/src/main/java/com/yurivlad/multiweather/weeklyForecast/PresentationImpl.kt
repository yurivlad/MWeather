package com.yurivlad.multiweather.weeklyForecast

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
class PresentationImpl : Presentation {
    @ExperimentalCoroutinesApi
    override fun getRootFragment(activity: AppCompatActivity): Fragment {
        return MainFragment()
    }
}