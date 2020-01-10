package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.YaApiService
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.BaseRepositoryImpl
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Ya10DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import trikita.log.Log

/**
 *
 */
@ExperimentalCoroutinesApi
class Ya10DayForecastRepositoryImpl(
    private val yaApiService: YaApiService,
    private val forecastConverter: ToDomainMapper<Ya10DayForecast, NoAdditionalParams, ForecastWithDayParts>,
    workerDispatcher: CoroutineDispatcher
) : BaseRepositoryImpl<ForecastWithDayParts, Ya10DayForecastRequest>(workerDispatcher) {


    override fun requestUpdate(request: Ya10DayForecastRequest) {
        Log.d("requestUpdate")
        val channel = getOrCreateChannel(request)
        if (channel.progressSendChannel.valueOrNull == true) {
            Log.d("declined, update in progress")
            return
        }
        launchHandledCoroutine(channel) {
            channel.valueSendChannel.offer(
                forecastConverter.convert(
                    yaApiService.get10DayForecast(request.lat, request.lon),
                    NoAdditionalParams
                )
            )
        }
    }
}