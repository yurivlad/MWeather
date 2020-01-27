package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.apiServiceModel.GisApiService
import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.domainModel.model.Gis10DayForecastRequest
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.persistence_model.DatabaseDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
class Gis10DayForecastRepositoryImpl(
    private val gisApiService: GisApiService,
    private val forecastConverter: ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts>,
    workerDispatcher: CoroutineDispatcher,
    private val forecastDatabase: DatabaseDomain<ForecastWithDayParts, ForecastSource>
) : BaseRepositoryImpl<ForecastWithDayParts, Gis10DayForecastRequest>(workerDispatcher) {

    override suspend fun onModelFetched(
        type: SourceType,
        value: ForecastWithDayParts,
        channel: CompositeBroadcastChannel<ForecastWithDayParts>) {
        super.onModelFetched(type, value, channel)
        if (type == SourceType.NETWORK) {
            forecastDatabase.remove(ForecastSource.GISMETEO)
            forecastDatabase.put(value)
        }
    }

    override suspend fun fetchModel(
        type: SourceType,
        request: Gis10DayForecastRequest
    ): ForecastWithDayParts? {
        return if (type == SourceType.CACHE)
            forecastDatabase.get(ForecastSource.GISMETEO)
        else forecastConverter.convert(
            gisApiService.get10DayForecast(request.cityUrl),
            NoAdditionalParams
        )
    }
}