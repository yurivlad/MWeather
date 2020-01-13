package com.yurivlad.multiweather.weeklyForecastImpl

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yurivlad.multiweather.weeklyForecastModel.Presentation
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