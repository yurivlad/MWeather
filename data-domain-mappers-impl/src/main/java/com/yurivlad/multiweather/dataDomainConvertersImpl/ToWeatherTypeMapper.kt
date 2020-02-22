package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import com.yurivlad.multiweather.dataDomainConvertersModel.ToWeatherTypeMapper
import com.yurivlad.multiweather.domainModel.model.WeatherList
import com.yurivlad.multiweather.domainModel.model.WeatherType
import java.util.*
import java.util.regex.Pattern

/**
 *
 */
object ToWeatherTypeMapperImpl : ToWeatherTypeMapper {
    override fun convert(from: String, additionalData: NoAdditionalParams) =
        from.toLowerCase(Locale.getDefault())
            .split(Pattern.compile(",|или|немного|возможна|возможно"))
            .filter { it.isNotBlank() }
            .map {
                fromString(
                    it
                        .trim()
                )
            }
            .distinct()
            .toSet()
            .run {
                WeatherList(this)
            }


    private fun fromString(str: String): WeatherType {
        return when (str) {
            "малооблачно", "переменная облачность", "перистыми облаками", "перистые облака", "кучевые облака", "облачно с прояснениями" -> WeatherType.CLOUDY
            "ясно", "ясная погода" -> WeatherType.CLEAR
            "снег с дождём", "дождь со снегом" -> WeatherType.SNOW_WITH_RAIN
            "пасмурно", "облачно", "кучево-дождевых облаков", "кучево-дождевые облака" -> WeatherType.MAINLY_CLOUDY
            "небольшой дождь", "незначительные осадки" -> WeatherType.SMALL_RAIN
            "дождь", "осадки", "дождем" -> WeatherType.RAIN
            "снег", "мокрый снег" -> WeatherType.SNOW
            "небольшой снег" -> WeatherType.SMALL_SNOW
            "гроза" -> WeatherType.STORM
            "ветер", "ветренно", "ветром", "сильный ветер", "сильным ветром" -> WeatherType.WINDY
            "сильный снег", "снегопад" -> WeatherType.HEAVY_SNOW
            "сильный дождь", "ливневыми дождями", "ливневые дожди", "ливневый дождь", "ливни", "ливень" -> WeatherType.HEAVY_RAIN
            "туман", "низкая слоистая облачность", "слоистая облачность", "дымка", "дымкой" -> WeatherType.FOG
            else -> {
                println("unable to parse $str")
                WeatherType.UNKNOWN
            }
        }
    }
}
