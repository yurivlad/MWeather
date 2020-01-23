package com.yurivlad.multiweather.apiServiceModel

import com.yurivlad.multiweather.parsersModel.Prim7DayForecast
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 */
interface PrimApiService {
    @GET("{cityName}/.week")
    suspend fun get7DayForecast(@Path("cityName", encoded = true) primCityName: String): Prim7DayForecast
}