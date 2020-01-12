package com.yurivlad.multiweather.apiServiceApi

import com.yurivlad.multiweather.apiServiceImpl.GisApiServiceImpl
import com.yurivlad.multiweather.apiServiceImpl.PrimApiServiceImpl
import com.yurivlad.multiweather.apiServiceImpl.YaApiServiceImpl
import com.yurivlad.multiweather.apiServiceModel.GisApiService
import com.yurivlad.multiweather.apiServiceModel.PrimApiService
import com.yurivlad.multiweather.apiServiceModel.YaApiService
import com.yurivlad.multiweather.constants.getRandomUserAgent
import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Parser
import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 *
 */
fun createOkHttpClient(dispatcher: Dispatcher) =
    OkHttpClient
        .Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .dispatcher(dispatcher)
        .followRedirects(false)
        .callTimeout(30_000, TimeUnit.SECONDS)
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                return chain.proceed(
                    chain
                        .request()
                        .newBuilder()
                        //.addHeader("user-agent", getRandomUserAgent())
                        .build()
                )
            }
        })
        .build()


fun createGisApiService(
    gis10DayForecastParser: Parser<Gis10DayForecast>,
    okHttpClient: OkHttpClient
): GisApiService =
    GisApiServiceImpl(gis10DayForecastParser, okHttpClient)

fun createPrimApiService(
    prim7DayForecastParser: Parser<Prim7DayForecast>,
    okHttpClient: OkHttpClient
): PrimApiService =
    PrimApiServiceImpl(prim7DayForecastParser, okHttpClient)

fun createYaApiService(
    ya10DayForecastParser: Parser<Ya10DayForecast>,
    okHttpClient: OkHttpClient
): YaApiService =
    YaApiServiceImpl(ya10DayForecastParser, okHttpClient)