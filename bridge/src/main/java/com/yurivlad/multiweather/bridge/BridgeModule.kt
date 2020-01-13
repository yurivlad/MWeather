@file:Suppress("RemoveExplicitTypeArguments")

package com.yurivlad.multiweather.bridge

import android.content.Context
import com.yurivlad.multiweather.apiServiceApi.createGisApiService
import com.yurivlad.multiweather.apiServiceApi.createOkHttpClient
import com.yurivlad.multiweather.apiServiceApi.createPrimApiService
import com.yurivlad.multiweather.apiServiceApi.createYaApiService
import com.yurivlad.multiweather.apiServiceModel.GisApiService
import com.yurivlad.multiweather.apiServiceModel.PrimApiService
import com.yurivlad.multiweather.apiServiceModel.YaApiService
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.core.DispatchersProviderImpl
import com.yurivlad.multiweather.core.StringsProvider
import com.yurivlad.multiweather.data.Gis10DayForecastRepositoryImpl
import com.yurivlad.multiweather.data.Prim7DayForecastRepositoryImpl
import com.yurivlad.multiweather.data.Ya10DayForecastRepositoryImpl
import com.yurivlad.multiweather.dataDomainConvertersImpl.GisToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersImpl.PrimToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersImpl.YaToDomainMapper
import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToDomainMapper
import com.yurivlad.multiweather.domainImpl.WeatherSourcesForecastUseCase
import com.yurivlad.multiweather.domainModel.RepositoryDomain
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.*
import com.yurivlad.multiweather.domainPresenterMappersImpl.ForecastWithDayPartsToPresenterConverter
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.domainPresenterMappersModel.ToPresenterMapper
import com.yurivlad.multiweather.parsersImpl.GisParserImpl
import com.yurivlad.multiweather.parsersImpl.PrimParserImpl
import com.yurivlad.multiweather.parsersImpl.YaParserImpl
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Parser
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import com.yurivlad.multiweather.presenterModel.ForecastWithThreeSourcesPresenterModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Dispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 *
 */
@ExperimentalCoroutinesApi
internal val appCoreModules = module {
    single<Parser<Gis10DayForecast>>(named("GisParserImpl")) { GisParserImpl }
    single<Parser<Ya10DayForecast>>(named("YaParserImpl")) { YaParserImpl }
    single<Parser<Prim7DayForecast>>(named("PrimParserImpl")) { PrimParserImpl }

    single<GisApiService> { createGisApiService(get(named("GisParserImpl")), get()) }
    single<YaApiService> { createYaApiService(get(named("YaParserImpl")), get()) }
    single<PrimApiService> { createPrimApiService(get(named("PrimParserImpl")), get()) }

    single<ToDomainMapper<Gis10DayForecast, NoAdditionalParams, ForecastWithDayParts>>(named("GisToDomainMapper")) { GisToDomainMapper }
    single<ToDomainMapper<Ya10DayForecast, NoAdditionalParams, ForecastWithDayParts>>(named("YaToDomainMapper")) { YaToDomainMapper }
    single<ToDomainMapper<Prim7DayForecast, NoAdditionalParams, ForecastWithDayParts>>(named("PrimToDomainMapper")) { PrimToDomainMapper }

    single<RepositoryDomain<ForecastWithDayParts, Gis10DayForecastRequest>>(named("Gis10DayForecastRepositoryImpl")) {
        Gis10DayForecastRepositoryImpl(
            get(),
            get(named("GisToDomainMapper")),
            get()
        )
    }
    single<RepositoryDomain<ForecastWithDayParts, Ya10DayForecastRequest>>(named("Ya10DayForecastRepositoryImpl"))
    {
        Ya10DayForecastRepositoryImpl(
            get(),
            get(named("YaToDomainMapper")),
            get()
        )
    }
    single<RepositoryDomain<ForecastWithDayParts, Prim7DayForecastRequest>>(named("Prim7DayForecastRepositoryImpl"))
    {
        Prim7DayForecastRepositoryImpl(
            get(),
            get(named("PrimToDomainMapper")),
            get()
        )
    }

    single<UseCase<ForecastSources, NoParamsRequest>> {
        WeatherSourcesForecastUseCase(
            get(),
            get(named("Gis10DayForecastRepositoryImpl")),
            get(named("Ya10DayForecastRepositoryImpl")),
            get(named("Prim7DayForecastRepositoryImpl"))
        )
    }
    single<ToPresenterMapper<ForecastSources, NoAdditionalData, ForecastWithThreeSourcesPresenterModel>>
    { ForecastWithDayPartsToPresenterConverter(get()) }
    single { createOkHttpClient(get()) }
}

val realAppDependencies = module {
    single<DispatchersProvider> { DispatchersProviderImpl }
    single<CoroutineDispatcher> { get<DispatchersProvider>().workerDispatcher }
    single<StringsProvider> {
        object : StringsProvider {
            override fun getString(resId: Int): String {
                return androidContext().getString(resId)
            }

            override fun getString(resId: Int, vararg formatArgs: Any): String {
                return androidContext().getString(resId, *formatArgs)
            }
        }
    }
    single { Dispatcher() }

}