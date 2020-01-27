package com.yurivlad.multiweather

import android.app.job.JobParameters
import android.app.job.JobService
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 *
 */
@ExperimentalCoroutinesApi
class JobServiceImpl : JobService(), KoinComponent {
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        get<UseCase<ForecastSources, NoParamsRequest>>().action(NoParamsRequest)
        return false
    }
}