package com.yurivlad.multiweather.domainModel.model

import com.yurivlad.multiweather.domainModel.RepositoryRequest

/**
 *
 */
data class Gis10DayForecastRequest(val cityUrl: String) :
    RepositoryRequest

data class Prim7DayForecastRequest(val cityName: String) :
    RepositoryRequest

data class Ya10DayForecastRequest(val lat: Double, val lon: Double) :
    RepositoryRequest