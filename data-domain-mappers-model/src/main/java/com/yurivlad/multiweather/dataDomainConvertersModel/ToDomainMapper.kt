package com.yurivlad.multiweather.dataDomainConvertersModel

import com.yurivlad.multiweather.domainModel.DomainModel

/**
 *
 */
interface ToDomainMapper<F, D : ToDomainMapperAdditionalParams, T : DomainModel> {
    fun convert(from: F, additionalData: D): T
}