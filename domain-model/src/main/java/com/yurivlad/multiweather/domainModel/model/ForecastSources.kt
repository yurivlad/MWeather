package com.yurivlad.multiweather.domainModel.model

import com.yurivlad.multiweather.core.CompositeReceiveChannel
import com.yurivlad.multiweather.domainModel.DomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
data class ForecastSources(val list: List<CompositeReceiveChannel<ForecastWithDayParts>>) : DomainModel,
    List<CompositeReceiveChannel<ForecastWithDayParts>> by list

