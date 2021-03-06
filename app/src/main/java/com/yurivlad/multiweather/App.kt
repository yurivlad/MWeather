package com.yurivlad.multiweather

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.net.NetworkRequest
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.yurivlad.multiweather.bridge.Bridge
import com.yurivlad.multiweather.navigation.AppSectionNavigation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import trikita.log.Log
import java.util.concurrent.Executors
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
            loadKoinModules(module { single<AppSectionNavigation> { AppSectionNavigationImpl(get()) } })
        }

        Bridge.onAppCreated()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        jobScheduler.schedule(
            JobInfo
                .Builder(1, ComponentName(this, JobServiceImpl::class.java))
                .setMinimumLatency(TimeUnit.HOURS.toMillis(1))
                .setBackoffCriteria(TimeUnit.HOURS.toMillis(1), JobInfo.BACKOFF_POLICY_LINEAR)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        setRequiredNetwork(
                            NetworkRequest
                                .Builder()
                                .build()
                        )
                        setPrefetch(true)
                        setEstimatedNetworkBytes(300 * 1024, 0L)//300 kb
                    }
                }
                .build()
        )

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            penaltyListener(Executors.newSingleThreadExecutor(),
                                StrictMode.OnThreadViolationListener {
                                    Log.e("Strict mode violation\n$it")
                                })
                        }
                    }
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }
    }
}