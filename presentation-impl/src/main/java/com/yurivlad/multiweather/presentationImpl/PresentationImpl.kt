package com.yurivlad.multiweather.presentationImpl

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yurivlad.multiweather.presentationModel.Presentation

/**
 *
 */
class PresentationImpl : Presentation {
    override fun getRootFragment(activity: AppCompatActivity): Fragment {
        return MainFragment()
    }
}