package com.yurivlad.multiweather.weeklyForecast.model

import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts

/**
 *
 */
data class ForecastsData(
    val firstForecast: ForecastWithDayParts?,
    val secondForecast: ForecastWithDayParts?,
    val thirdForecast: ForecastWithDayParts?,
    val isUpdateGoing: Boolean,
    val errorMessage: String?
)