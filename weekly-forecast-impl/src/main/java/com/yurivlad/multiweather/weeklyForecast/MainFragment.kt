package com.yurivlad.multiweather.weeklyForecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.get

/**
 *
 */
@ExperimentalCoroutinesApi
class MainFragment : BaseFragment<MainFragmentViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun createViewModel(): MainFragmentViewModel {
        return ViewModelProviders.of(this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainFragmentViewModel(get(), get()) as T
                }
            }).get(MainFragmentViewModel::class.java)
    }
}