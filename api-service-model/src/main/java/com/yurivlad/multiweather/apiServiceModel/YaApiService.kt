package com.yurivlad.multiweather.apiServiceModel

import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */
interface YaApiService {
    @GET("details/")
    suspend fun get10DayForecast(
        @Query("lat") cityLatitude: Double,
        @Query("lon") cityLongitude: Double,
        @Query("cameras") cameras: Int = 0
    ): Ya10DayForecast
}