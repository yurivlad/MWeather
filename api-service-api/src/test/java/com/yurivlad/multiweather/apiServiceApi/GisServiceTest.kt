package com.yurivlad.multiweather.apiServiceApi

import com.yurivlad.multiweather.parsersModel.Gis10DayForecast
import com.yurivlad.multiweather.parsersModel.Parser
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
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
            })

            service.get10DayForecast("weather-nakhodka-4879")
        }
    }

    ////api/v2/search/nearestTownsByCoords/?latitude=" + t.latitude + "&longitude=" + t.longitude + "&limit=1"
    //https://www.gismeteo.ru/api/v2/weather/current/11825/
    @Test
    fun apiTest() {
        val okHttpClient = OkHttpClient
            .Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()

        okHttpClient.newCall(Request.Builder().get().url("https://www.gismeteo.ru/weather-nakhodka-4879/3-days/#7-9-days").build())
            .execute()
    }
}