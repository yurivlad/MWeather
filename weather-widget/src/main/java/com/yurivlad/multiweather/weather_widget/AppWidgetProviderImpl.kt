package com.yurivlad.multiweather.weather_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import com.yurivlad.multiweather.domainPresenterMappersModel.DayOfMonthMapperParam
import com.yurivlad.multiweather.domainPresenterMappersModel.WeatherWidgetMapper
import com.yurivlad.multiweather.navigation.AppSectionNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import java.util.*

/**
 *
 */
@ExperimentalCoroutinesApi
internal class AppWidgetProviderImpl : AppWidgetProvider(), KoinComponent {
    private val useCase: UseCase<ForecastSources, NoParamsRequest> by inject()
    private val mapper: WeatherWidgetMapper by inject()
    private val appSectionNavigation: AppSectionNavigation by inject()
    private val scope = CoroutineScope(get<DispatchersProvider>().mainDispatcher)

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidget(context ?: return, appWidgetManager ?: return, appWidgetIds ?: return)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        updateWidget(
            context ?: return,
            context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager,
            newWidgetIds ?: return
        )
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateWidget(context ?: return, appWidgetManager ?: return, intArrayOf(appWidgetId))
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val currentDay =
            Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"))[Calendar.DAY_OF_MONTH]

        useCase.action(NoParamsRequest)
        scope.launch {

            val forecast = useCase.resultChannel.openValueSubscription().receive()
            val model = mapper.convert(forecast, DayOfMonthMapperParam(currentDay))

            appWidgetIds.forEach { id ->

                val remoteView = RemoteViews(context.packageName, R.layout.widget_layout)

                remoteView.setImageViewResource(
                    R.id.weather_night,
                    model.nighForecast?.weatherImageSrc ?: 0
                )
                remoteView.setImageViewResource(
                    R.id.weather_morning,
                    model.morningForecast?.weatherImageSrc ?: 0
                )
                remoteView.setImageViewResource(
                    R.id.weather_day,
                    model.dayForecast?.weatherImageSrc ?: 0
                )
                remoteView.setImageViewResource(
                    R.id.weather_evening,
                    model.eveningForecast?.weatherImageSrc ?: 0
                )

                remoteView.setTextViewText(
                    R.id.temp_night,
                    context.getString(
                        R.string.temperature,
                        model.nighForecast?.temperatureCelsius ?: ""
                    )
                )
                remoteView.setTextViewText(
                    R.id.temp_morning,
                    context.getString(
                        R.string.temperature,
                        model.morningForecast?.temperatureCelsius ?: ""
                    )
                )
                remoteView.setTextViewText(
                    R.id.temp_day,
                    context.getString(
                        R.string.temperature,
                        model.dayForecast?.temperatureCelsius ?: ""
                    )
                )
                remoteView.setTextViewText(
                    R.id.temp_evening,
                    context.getString(
                        R.string.temperature,
                        model.eveningForecast?.temperatureCelsius ?: ""
                    )
                )

                remoteView.setTextViewText(
                    R.id.wind_night,
                    context.getString(R.string.wind, model.nighForecast?.windMetersPerSecond ?: "")
                )
                remoteView.setTextViewText(
                    R.id.wind_morning,
                    context.getString(
                        R.string.wind,
                        model.morningForecast?.windMetersPerSecond ?: ""
                    )
                )
                remoteView.setTextViewText(
                    R.id.wind_day,
                    context.getString(R.string.wind, model.dayForecast?.windMetersPerSecond ?: "")
                )
                remoteView.setTextViewText(
                    R.id.wind_evening,
                    context.getString(
                        R.string.wind,
                        model.eveningForecast?.windMetersPerSecond ?: ""
                    )
                )

                appWidgetManager.updateAppWidget(id, remoteView)

                remoteView.setOnClickPendingIntent(
                    R.id.widget_lo_root,
                    appSectionNavigation.getWeeklyForecastPendingIntent()
                )
            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        useCase.action(NoParamsRequest)
    }
}