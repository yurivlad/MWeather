package com.yurivlad.multiweather.domainPresenterMappersImpl

import com.yurivlad.multiweather.domainModel.model.DayPart
import com.yurivlad.multiweather.domainModel.model.WeatherList
import com.yurivlad.multiweather.domainModel.model.WeatherType
import com.yurivlad.multiweather.sharedResources.R

/**
 *
 */

internal fun WeatherList.convertToDrawableRes(dayPart: DayPart): Int {

    return when {
        isEmpty() -> 0
        size == 1 -> {//simple weather
            first().toDrawableRes(dayPart)
        }
        else -> {//complex_weather
            when {
                contains(WeatherType.WINDY)
                        && (contains(WeatherType.CLOUDY) || contains(WeatherType.MAINLY_CLOUDY)) -> R.drawable.ic_cloudy_wind_linear_40dp
                contains(WeatherType.STORM) && (contains(WeatherType.RAIN) || contains(
                    WeatherType.HEAVY_RAIN
                ) || contains(WeatherType.SMALL_RAIN)) -> R.drawable.ic_storm_rain_linear_40dp
                contains(WeatherType.STORM) && (contains(WeatherType.SNOW) || contains(
                    WeatherType.HEAVY_SNOW
                ) || contains(WeatherType.SMALL_SNOW)) -> R.drawable.ic_storm_with_snow_linear_40dp
                contains(WeatherType.CLEAR) && (contains(WeatherType.RAIN) || contains(
                    WeatherType.SMALL_RAIN
                )) && dayPart != DayPart.NIGHT -> R.drawable.ic_rain_clear_linear_40dp
                contains(WeatherType.CLEAR) && (contains(WeatherType.CLOUDY) || contains(
                    WeatherType.MAINLY_CLOUDY
                )) && dayPart != DayPart.NIGHT -> R.drawable.ic_main_cloudy_clear_day_linear_40dp
                contains(WeatherType.SMALL_SNOW) && (contains(WeatherType.CLOUDY) || contains(
                    WeatherType.MAINLY_CLOUDY
                )) -> R.drawable.ic_light_snow_linear_40dp
                else -> first().toDrawableRes(dayPart)
            }
        }
    }
}

internal fun WeatherType.toDrawableRes(dayPart: DayPart): Int {
    return when (this) {
        WeatherType.CLOUDY -> if (dayPart == DayPart.NIGHT) R.drawable.ic_cloudy_night_linear_40dp else R.drawable.ic_cloudy_clear_day_linear_40dp
        WeatherType.CLEAR -> if (dayPart == DayPart.NIGHT) R.drawable.ic_clear_night_linear_40dp else R.drawable.ic_clear_linear_40dp
        WeatherType.SNOW_WITH_RAIN -> R.drawable.ic_snow_rain_linear_40dp
        WeatherType.MAINLY_CLOUDY -> if (dayPart == DayPart.NIGHT) R.drawable.ic_mainly_coudy_night_linear_40dp else R.drawable.ic_mainly_cloudy_day_linear_40dp
        WeatherType.SMALL_RAIN -> R.drawable.ic_rain_linear_40dp
        WeatherType.RAIN -> R.drawable.ic_rain_small_linear_40dp
        WeatherType.HEAVY_RAIN -> R.drawable.ic_heavy_rain_linear_40dp
        WeatherType.SNOW -> R.drawable.ic_snow_linear_40dp
        WeatherType.SMALL_SNOW -> R.drawable.ic_light_snow_linear_40dp
        WeatherType.HEAVY_SNOW -> R.drawable.ic_heavy_snow_linear_40dp
        WeatherType.STORM -> R.drawable.ic_storm_linear_40dp
        WeatherType.FOG -> if (dayPart == DayPart.NIGHT) R.drawable.ic_foggy_night_linear_40dp else R.drawable.ic_foggy_day_linear_40dp
        WeatherType.WINDY -> R.drawable.ic_windy_linear_40dp
        WeatherType.UNKNOWN -> 0
    }
}

internal fun Double.formatDouble(): String =
    if (this % 1 == 0.0) toInt().toString() else toString()