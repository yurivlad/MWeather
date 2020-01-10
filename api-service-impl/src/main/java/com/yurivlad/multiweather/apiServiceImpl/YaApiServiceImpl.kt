package com.yurivlad.multiweather.apiServiceImpl

import com.yurivlad.multiweather.apiServiceModel.YaApiService
import com.yurivlad.multiweather.constants.YA_BASE_URL
import com.yurivlad.multiweather.parsersModel.Parser
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import okhttp3.OkHttpClient

/**
 *
 */

class YaApiServiceImpl(
    ya10DayForecastParser: Parser<Ya10DayForecast>,
    okHttpClient: OkHttpClient
) : YaApiService by createService(okHttpClient, YA_BASE_URL, listOf(factory(ya10DayForecastParser)))



