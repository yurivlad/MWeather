package com.yurivlad.multiweather

import android.app.job.JobParameters
import android.app.job.JobService
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import trikita.log.Log

/**
 *
 */
@ExperimentalCoroutinesApi
class JobServiceImpl : JobService(), KoinComponent {
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val useCase = get<UseCase<ForecastSources, NoParamsRequest>>()
        useCase.action(NoParamsRequest)
        CoroutineScope(get<DispatchersProvider>().mainDispatcher).launch {

            val progressChannel = useCase.resultChannel.openProgressSubscription()
            @Suppress("ControlFlowWithEmptyBody")
            while (!progressChannel.receive()){}
            jobFinished(params, true)
        }

        return true
    }
}