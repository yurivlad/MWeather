package com.yurivlad.multiweather.apiServiceApi

import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Parser
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import java.util.*

/**
 *
 */
class GisServiceTest {

    @Test
    fun gisTest() {
        runBlocking {
            val service = createGisApiService(object : Parser<Gis10DayForecast> {
                override fun parse(inputHtml: String): Gis10DayForecast {
                    return Gis10DayForecast(Date(), Date(), emptyList())
                }
            }, OkHttpClient())

            service.get10DayForecast("weather-nakhodka-4879")
        }
    }


}