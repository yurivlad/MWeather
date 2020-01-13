package com.yurivlad.multiweather.weeklyForecastModel

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yurivlad.multiweather.weeklyForecastImpl.WeeklyForecastFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
class PresentationImpl : Presentation {
    @ExperimentalCoroutinesApi
    override fun getRootFragment(activity: AppCompatActivity): Fragment {
        return WeeklyForecastFragment()
    }
}