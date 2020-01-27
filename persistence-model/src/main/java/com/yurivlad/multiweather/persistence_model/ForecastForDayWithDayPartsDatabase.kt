package com.yurivlad.multiweather.persistence_model

import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts

/**
 *
 */
interface ForecastForDayWithDayPartsDatabaseDomain : DatabaseDomain<ForecastWithDayParts, ForecastSource>