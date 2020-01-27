package com.yurivlad.multiweather.apiServiceImpl

import com.yurivlad.multiweather.apiServiceModel.GisApiService
import com.yurivlad.multiweather.constants.GIS_BASE_URL
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Parser
import okhttp3.OkHttpClient

/**
 *
 */

class GisApiServiceImpl(
    gis10DayForecastParser: Parser<Gis10DayForecast>,
    okHttpClient: OkHttpClient
) : GisApiService by createService(
    okHttpClient,
    GIS_BASE_URL,
    listOf(
        factory(gis10DayForecastParser)
    )
)





