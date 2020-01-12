package com.yurivlad.multiweather.domainPresenterMappersModel

import com.yurivlad.multiweather.domainModel.DomainModel
import com.yurivlad.multiweather.presenterModel.PresenterModel

/**
 *
 */
interface ToPresenterMapper<F : DomainModel, D : ToPresenterMapperParams, T : PresenterModel> {
    fun convert(from: F, additionalData: D): T
}