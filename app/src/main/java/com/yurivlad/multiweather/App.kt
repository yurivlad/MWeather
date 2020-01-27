package com.yurivlad.multiweather

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.yurivlad.multiweather.bridge.Bridge
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

/**
 *
 */
@ExperimentalCoroutinesApi
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
        }
        Bridge.onAppCreated()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobId = 1

        if (!jobScheduler.allPendingJobs.any { it.id == jobId }) {
            jobScheduler.schedule(
                JobInfo
                    .Builder(1, ComponentName(this, JobServiceImpl::class.java))
                    .setPeriodic(TimeUnit.HOURS.toMillis(1))
                    .apply {
                        if (Build.VERSION.SDK_INT == 28) {
                            setPrefetch(true)
                            setEstimatedNetworkBytes(8 * 1024 * 1024, 0L)
                        }
                    }
                    .build()
            )
        }


    }
}