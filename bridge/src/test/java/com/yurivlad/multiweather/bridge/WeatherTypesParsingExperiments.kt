@file:Suppress("BlockingMethodInNonBlockingContext")

package com.yurivlad.multiweather.bridge

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yurivlad.multiweather.apiServiceModel.PrimApiService
import com.yurivlad.multiweather.domainModel.model.WeatherType
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.io.File
import java.io.FileWriter
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

/**
 *
 */
@ExperimentalCoroutinesApi
@Suppress("UNUSED_VARIABLE")
class WeatherTypesParsingExperiments : KoinTest {
    @ExperimentalCoroutinesApi
    @Test
    fun gisExperiments() {
        runBlocking {
            startKoin {
                loadKoinModules(listOf(appCoreModules, testModule))
            }
            ////api/v2/search/nearestTownsByCoords/?latitude=" + t.latitude + "&longitude=" + t.longitude + "&limit=1"
            //https://www.gismeteo.ru/api/v2/weather/current/11825/

            val okHttpClient = OkHttpClient
                .Builder()
                .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                })
                .build()

//        okHttpClient.newCall(Request.Builder().get().url("https://www.gismeteo.ru/weather-nakhodka-4879/3-days/#7-9-days").build())
//            .execute()

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val max = 85.05112878
            val min = -85.05112878

            val text = File("./summaries.txt").readText()
            val adapter = moshi.adapter<List<List<String>>>(
                Types.newParameterizedType(
                    List::class.java,
                    List::class.java,
                    String::class.java
                )
            )

            val result = adapter.fromJson(text)!!.flatten().toSet().map { parseWeather(it) }

            Assert.assertTrue(!result.flatten().contains(WeatherType.UNKNOWN))

            println(result)
        }
    }

    @Test
    fun primExperiments() = runBlocking {
        startKoin {
            loadKoinModules(listOf(appCoreModules, testModule))
        }

        val citiesHtml = (this::class.java).getResource("/prim_cities.html")!!.readText()
        val regex = Regex("/weather/[a-z_1-9\\-]+(/([a-z_1-9]+))?")
        var matchResult = regex.find(citiesHtml, 0)
        val cities = ArrayList<String>()
        while (matchResult != null) {
            cities.add(matchResult.value.replace("/weather/", ""))
            matchResult = matchResult.next()
        }

        val primApiSequence = get<PrimApiService>()

        val latch = CountDownLatch(cities.size)
        val summaries = HashSet<String>()
        val errorCities = ArrayList<String>()

        cities.forEach { city ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val resp = primApiSequence.get7DayForecast(city)
                    resp.foreCasts.forEach {
                        summaries.add(it.dayForecast.summary)
                        summaries.add(it.eveningForecast.summary)
                        summaries.add(it.nightForecast.summary)
                        summaries.add(it.morningForecast.summary)
                    }
                } catch (e: Exception) {
                    errorCities.add(city)
                    e.printStackTrace()
                } finally {
                    latch.countDown()

                }
            }
        }



        latch.await()

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val fw = FileWriter(File("./summaries_prim.json"))
        val fw1 = FileWriter(File("./error_cities.json"))
        fw.append(
            moshi.adapter<Set<String>>(
                Types.newParameterizedType(
                    Set::class.java,
                    String::class.java
                )
            ).toJson(summaries)
        )
        fw1.append(
            moshi.adapter<List<String>>(
                Types.newParameterizedType(
                    List::class.java,
                    String::class.java
                )
            ).toJson(errorCities)
        )
        fw.flush()
        fw1.flush()

        delay(10_000)
    }

    @Test
    fun testPrimSummaries() {
        runBlocking {
            startKoin {
                loadKoinModules(listOf(appCoreModules, testModule))
            }

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val summaries = (moshi.adapter<List<String>>(
                Types.newParameterizedType(
                    List::class.java,
                    String::class.java
                )
            ).fromJson(File("./summaries_prim.json").readText()))!!

            Assert.assertTrue(!summaries.map { parseWeather(it) }.any { it.contains(WeatherType.UNKNOWN) })
        }
    }

    @Test
    fun yaExperiments() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adb = moshi.adapter<Set<String>>(
            Types.newParameterizedType(
                Set::class.java,
                String::class.java
            )
        )
        val str =
            "[\"Дождь\",\"Малооблачно\",\"Пасмурно\",\"Ясно\",\"Ливни\",\"Облачно с прояснениями\",\"Небольшой снег\",\"Снег\",\"Небольшой дождь\",\"Дождь со снегом\",\"Ливень\",\"Снегопад\"]"
        val result = adb.fromJson(str)!!.groupBy { it }.mapValues {
            parseWeather(it.value.first())
        }
        println(result)
    }
}


fun parseWeather(from: String): List<WeatherType> =
    from.toLowerCase().split(Pattern.compile(",|или|\\bс\\b|немного|возможна|возможно"))
        .filter { it.isNotBlank() }
        .map {
            fromString(
                it
                    .trim()
            )
        }.distinct()

fun fromString(str: String): WeatherType {
    return when (str) {
        "малооблачно", "облачно", "переменная облачность", "кучево-дождевых облаков",
        "кучево-дождевые облака", "перистыми облаками", "перистые облака", "кучевые облака", "прояснениями" -> WeatherType.CLOUDY
        "ясно", "ясная погода" -> WeatherType.CLEAR
        "снег с дождём", "дождь со снегом" -> WeatherType.SNOW_WITH_RAIN
        "пасмурно" -> WeatherType.MAINLY_CLOUDY
        "небольшой дождь" -> WeatherType.SMALL_RAIN
        "дождь", "осадки" -> WeatherType.RAIN
        "снег", "мокрый снег" -> WeatherType.SNOW
        "небольшой снег" -> WeatherType.SMALL_SNOW
        "гроза" -> WeatherType.STORM
        "сильный снег", "снегопад" -> WeatherType.HEAVY_SNOW
        "сильный дождь", "ливневыми дождями", "ливневые дожди", "ливневый дождь", "ливни", "ливень" -> WeatherType.HEAVY_RAIN
        "туман", "низкая слоистая облачность", "слоистая облачность", "дымка", "дымкой" -> WeatherType.FOG
        else -> {
            println("unable to parse $str")
            WeatherType.UNKNOWN
        }
    }
}