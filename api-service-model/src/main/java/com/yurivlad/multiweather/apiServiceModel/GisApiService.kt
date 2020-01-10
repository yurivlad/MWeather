package com.yurivlad.multiweather.apiServiceModel

import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 */
interface GisApiService {
    @GET("{path}/3-days/#7-9-days")
    suspend fun get10DayForecast(
        @Path("path") forecastPagePath: String
    ): Gis10DayForecast
}