package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.PrimApiService
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.BaseRepositoryImpl
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Prim7DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import trikita.log.Log

/**
 *
 */
@ExperimentalCoroutinesApi
class Prim7DayForecastRepositoryImpl(
    private val gisApiService: PrimApiService,
    private val forecastConverter: ToDomainMapper<Prim7DayForecast, NoAdditionalParams, ForecastWithDayParts>,
    workerDispatcher: CoroutineDispatcher
) : BaseRepositoryImpl<ForecastWithDayParts, Prim7DayForecastRequest>(workerDispatcher) {


    override fun requestUpdate(request: Prim7DayForecastRequest) {
        Log.d("requestUpdate")
        val channel = getOrCreateChannel(request)
        if (channel.progressSendChannel.valueOrNull == true) {
            Log.d("declined, update in progress")
            return
        }
        launchHandledCoroutine(channel) {
            channel.valueSendChannel.send(
                forecastConverter.convert(
                    gisApiService.get7DayForecast(request.cityName),
                    NoAdditionalParams
                )
            )
        }
    }
}