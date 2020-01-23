package com.yurivlad.multiweather.dataDomainConvertersModel

import com.yurivlad.multiweather.domainModel.model.WeatherList

/**
 *
 */
interface ToWeatherTypeMapper : ToDomainMapper<String, NoAdditionalParams, WeatherList>