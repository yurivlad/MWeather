package com.yurivlad.multiweather.domainModel.model

import com.yurivlad.multiweather.domainModel.DomainModel

/**
 *
 */
enum class WeatherType : DomainModel {
    CLEAR,
    CLOUDY,
    MAINLY_CLOUDY,
    SMALL_RAIN,
    RAIN,
    SNOW_WITH_RAIN,
    HEAVY_RAIN,
    SNOW,
    SMALL_SNOW,
    HEAVY_SNOW,
    STORM,
    WINDY,
    FOG,
    UNKNOWN
}

data class WeatherList(val list: Set<WeatherType>) : DomainModel, Set<WeatherType> by list