package com.yurivlad.multiweather.apiServiceImpl

import com.yurivlad.multiweather.parsersModel.Parser
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 *
 */
internal inline fun <reified T> createService(
    okHttpClient: OkHttpClient,
    baseUrl: String,
    factories: List<Converter.Factory>
): T {
    return Retrofit
        .Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .also { retrofitBuilder ->
            factories.forEach { retrofitBuilder.addConverterFactory(it) }
        }
        .build()
        .create(T::class.java)
}

internal inline fun <reified T> converter(parser: Parser<T>) =
    Converter<ResponseBody, T> {
        parser.parse(it.string())
    }

internal inline fun <reified T> factory(parser: Parser<T>) = object : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, T>? {
        return converter(parser)
    }
}


