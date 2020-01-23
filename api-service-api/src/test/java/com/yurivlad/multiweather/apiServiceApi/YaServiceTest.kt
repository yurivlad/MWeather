package com.yurivlad.multiweather.apiServiceApi

import com.yurivlad.multiweather.parsersModel.Parser
import com.yurivlad.multiweather.parsersModel.Ya10DayForecast
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import java.util.*

/**
 *
 */
class YaServiceTest {

    @Test
    fun yaFetchTest() {
        runBlocking{
            val service = createYaApiService(object : Parser<Ya10DayForecast> {
                override fun parse(inputHtml: String): Ya10DayForecast {
                    return Ya10DayForecast(Date(), Date(), emptyList())
                }
            }, OkHttpClient())

            service.get10DayForecast(42.824037, 132.892811)
        }
    }
}