package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.YaApiService
import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Ya10DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import com.yurivlad.multiweather.persistence_model.DatabaseDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
class Ya10DayForecastRepositoryImpl(
    private val yaApiService: YaApiService,
    private val forecastConverter: ToDomainMapper<Ya10DayForecast, NoAdditionalParams, ForecastWithDayParts>,
    workerDispatcher: CoroutineDispatcher,
    private val forecastDatabase: DatabaseDomain<ForecastWithDayParts, ForecastSource>
) : BaseRepositoryImpl<ForecastWithDayParts, Ya10DayForecastRequest>(workerDispatcher) {

    override suspend fun onModelFetched(
        type: SourceType,
        value: ForecastWithDayParts,
        channel: CompositeBroadcastChannel<ForecastWithDayParts>
    ) {
        super.onModelFetched(type, value, channel)
        if (type == SourceType.NETWORK) {
            forecastDatabase.remove(ForecastSource.YANDEX)
            forecastDatabase.put(value)
        }
    }

    override suspend fun fetchModel(
        type: SourceType,
        request: Ya10DayForecastRequest
    ): ForecastWithDayParts? {
        return if (type == SourceType.NETWORK) forecastConverter.convert(
            yaApiService.get10DayForecast(request.lat, request.lon),
            NoAdditionalParams
        ) else forecastDatabase.get(ForecastSource.YANDEX)
    }
}