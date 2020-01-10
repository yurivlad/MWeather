package com.yurivlad.multiweather.apiServiceImpl

import com.yurivlad.multiweather.apiServiceModel.PrimApiService
import com.yurivlad.multiweather.constants.PRIM_BASE_URL
import com.yurivlad.multiweather.parsersModel.Parser
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import okhttp3.OkHttpClient

/**
 *
 */

class PrimApiServiceImpl(
    prim7DayForecastParser: Parser<Prim7DayForecast>,
    okHttpClient: OkHttpClient
) : PrimApiService by createService(
    okHttpClient, PRIM_BASE_URL, listOf(
        factory(prim7DayForecastParser)
    )
)



