package com.yurivlad.multiweather.dataDomainConvertersImpl

import com.yurivlad.multiweather.dataDomainConvertersModel.NoAdditionalParams
import org.junit.Test

/**
 *
 */
class ToWeatherMapperTest {
    @Test
    fun test(){
        val result = ToWeatherTypeMapperImpl.convert("Пасмурно, небольшой снег", NoAdditionalParams)
        println(result)
    }
}