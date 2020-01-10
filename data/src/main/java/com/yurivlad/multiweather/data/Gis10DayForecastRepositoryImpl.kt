package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.GisApiService
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.BaseRepositoryImpl
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Gis10DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import trikita.log.Log

/**
 *
 */
@ExperimentalCoroutinesApi
class Gis10DayForecastRepositoryImpl(
    private val gisApiService: GisApiService,
    private val forecastConverter: ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts>,
    workerDispatcher: CoroutineDispatcher
) : BaseRepositoryImpl<ForecastWithDayParts, Gis10DayForecastRequest>(workerDispatcher) {


    override fun requestUpdate(request: Gis10DayForecastRequest) {
        Log.d("requestUpdate")
        val channel = getOrCreateChannel(request)
        if (channel.progressOrNull == true) {
            Log.d("declined, update in progress")
            return
        }
        launchHandledCoroutine(channel) {
            channel.valueSendChannel.offer(
                forecastConverter.convert(
                    gisApiService.get10DayForecast(request.cityUrl),
                    NoAdditionalParams
                )
            )
        }
    }
}