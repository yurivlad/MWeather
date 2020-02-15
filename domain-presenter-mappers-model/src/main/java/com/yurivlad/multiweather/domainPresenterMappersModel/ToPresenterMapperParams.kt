package com.yurivlad.multiweather.domainPresenterMappersModel

/**
 *
 */
interface ToPresenterMapperParams

object NoAdditionalData : ToPresenterMapperParams

class DayOfMonthMapperParam(val day: Int) : ToPresenterMapperParams