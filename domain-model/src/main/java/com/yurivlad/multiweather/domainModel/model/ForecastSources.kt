package com.yurivlad.multiweather.domainModel.model

import com.yurivlad.multiweather.domainModel.DomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
data class ForecastSources(val list: List<ForecastWithDayParts>) : DomainModel,
    List<ForecastWithDayParts> by list

