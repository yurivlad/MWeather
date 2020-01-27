package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.PrimApiService
import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Prim7DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import com.yurivlad.multiweather.persistence_model.DatabaseDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
class Prim7DayForecastRepositoryImpl(
    private val gisApiService: PrimApiService,
    private val forecastConverter: ToDomainMapper<Prim7DayForecast, NoAdditionalParams, ForecastWithDayParts>,
    workerDispatcher: CoroutineDispatcher,
    private val forecastDatabase: DatabaseDomain<ForecastWithDayParts, ForecastSource>
) : BaseRepositoryImpl<ForecastWithDayParts, Prim7DayForecastRequest>(workerDispatcher) {

    override suspend fun onModelFetched(
        type: SourceType,
        value: ForecastWithDayParts,
        channel: CompositeBroadcastChannel<ForecastWithDayParts>
    ) {
        super.onModelFetched(type, value, channel)
        if (type == SourceType.NETWORK) {
            forecastDatabase.remove(ForecastSource.PRIMPOGODA)
            forecastDatabase.put(value)
        }
    }

    override suspend fun fetchModel(
        type: SourceType,
        request: Prim7DayForecastRequest
    ): ForecastWithDayParts? {
        return if (type == SourceType.NETWORK)
            forecastConverter.convert(
                gisApiService.get7DayForecast(request.cityName),
                NoAdditionalParams
            )
        else forecastDatabase.get(ForecastSource.PRIMPOGODA)
    }
}