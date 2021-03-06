package com.yurivlad.multiweather.weeklyForecastModel

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

/**
 *
 */
abstract class BaseFragment<VM : StatefulViewModel> : Fragment() {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        viewModel.onComponentStart(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.onSaveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onComponentStop()
    }

    abstract fun createViewModel(): VM

}