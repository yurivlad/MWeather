package com.yurivlad.multiweather.domainPresenterMappersModel

import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.presenterModel.ForecastWithThreeSourcesPresenterModel
import com.yurivlad.multiweather.presenterModel.WeatherWidgetModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
interface WeeklyForecastMapper :
    ToPresenterMapper<ForecastSources, NoAdditionalData, ForecastWithThreeSourcesPresenterModel>

@ExperimentalCoroutinesApi
interface WeatherWidgetMapper :
    ToPresenterMapper<ForecastSources, DayOfMonthMapperParam, WeatherWidgetModel>