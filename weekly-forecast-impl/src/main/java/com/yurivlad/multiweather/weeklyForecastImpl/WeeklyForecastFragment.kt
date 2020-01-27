package com.yurivlad.multiweather.weeklyForecastImpl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.yurivlad.multiweather.weeklyForecastImpl.databinding.MainFragmentBinding
import com.yurivlad.multiweather.weeklyForecastModel.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.get

/**
 *
 */
@ExperimentalCoroutinesApi
class WeeklyForecastFragment : BaseFragment<WeeklyForecastViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = MainFragmentBinding.inflate(inflater, container, false)
        db.lifecycleOwner = this
        db.viewModel = viewModel
        return db.root
    }


    override fun createViewModel(): WeeklyForecastViewModel {
        return ViewModelProviders.of(this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return WeeklyForecastViewModel(get(), get(), get()) as T
                }
            }).get(WeeklyForecastViewModel::class.java)
    }
}