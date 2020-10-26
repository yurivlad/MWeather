package com.yurivlad.multiweather.bridge

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import org.junit.Test

/**
 *
 */
class PositioningExperiments {
    val client = OkHttpClient
        .Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .build()

    @Test
    fun testGisPosition(){
        client.newCall(
            Request.Builder()
                .post(object: RequestBody() {
                    override fun contentType(): MediaType? {
                       return null
                    }

                    override fun writeTo(sink: BufferedSink) {
                        
                    }
                })
                .url("https://www.gismeteo.ru/api/v2/nearestTownsByCoords?latitude=42.8138400&longitude=132.8734800&limit=1")
                .build()
        ).execute()
    }
}