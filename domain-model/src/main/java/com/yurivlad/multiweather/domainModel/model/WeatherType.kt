package com.yurivlad.multiweather.domainModel.model

import com.yurivlad.multiweather.domainModel.DomainModel

/**
 *
 */
enum class WeatherType : DomainModel {
    CLOUDY,
    CLEAR,
    SNOW_WITH_RAIN,
    MAINLY_CLOUDY,
    SMALL_RAIN,
    RAIN, SNOW,
    SMALL_SNOW,
    STORM,
    WINDY,
    HEAVY_SNOW,
    HEAVY_RAIN,
    FOG,
    UNKNOWN
}

data class WeatherList(val list: Set<WeatherType>) : DomainModel, Set<WeatherType> by list