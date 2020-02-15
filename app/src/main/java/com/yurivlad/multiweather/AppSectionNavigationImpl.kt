package com.yurivlad.multiweather

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yurivlad.multiweather.navigation.AppSectionNavigation
import java.lang.ref.WeakReference

/**
 *
 */
class AppSectionNavigationImpl(context: Context) : AppSectionNavigation {
    private val contextRef = WeakReference<Context>(context.applicationContext)
    override fun openWeeklyForecastScreen() {

        val context = getContextOrThrow()

        context.startActivity(Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    override fun getWeeklyForecastPendingIntent(): PendingIntent {
        val context = getContextOrThrow()
        return PendingIntent.getActivity(context, 0, Intent(
            context, MainActivity::class.java
        ).apply
        {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }, 0
        )
    }

    private fun getContextOrThrow() =
        contextRef.get() ?: throw IllegalArgumentException("contextRef was gc'd")
}