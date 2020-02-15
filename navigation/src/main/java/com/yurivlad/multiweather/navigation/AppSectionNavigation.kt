package com.yurivlad.multiweather.navigation

import android.app.PendingIntent

/**
 *
 */
interface AppSectionNavigation {
    @Throws(IllegalStateException::class)
    fun openWeeklyForecastScreen()

    @Throws(IllegalStateException::class)
    fun getWeeklyForecastPendingIntent(): PendingIntent
}