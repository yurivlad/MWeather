package com.yurivlad.multiweather.presentationImpl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.yurivlad.multiweather.presentationModel.BaseFragment

/**
 *
 */
class MainFragment : BaseFragment<MainFragmentViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun createViewModel(): MainFragmentViewModel {
        return ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
    }
}