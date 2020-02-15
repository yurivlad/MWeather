package com.yurivlad.multiweather.presenterModel

import androidx.annotation.DrawableRes

/**
 *
 */

data class WeatherWidgetPayPart(
    @DrawableRes val weatherImageSrc: Int,
    val temperatureCelsius: String,
    val windMetersPerSecond: String
) : PresenterModel

data class WeatherWidgetModel(
    val nighForecast: WeatherWidgetPayPart?,
    val morningForecast: WeatherWidgetPayPart?,
    val dayForecast: WeatherWidgetPayPart?,
    val eveningForecast: WeatherWidgetPayPart?
):PresenterModel