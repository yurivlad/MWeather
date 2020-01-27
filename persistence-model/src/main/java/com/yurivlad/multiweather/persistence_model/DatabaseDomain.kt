package com.yurivlad.multiweather.persistence_model

import com.yurivlad.multiweather.domainModel.DomainModel

/**
 *
 */

interface DatabaseDomain<D : DomainModel, ID> {
    suspend fun put(item: D)
    suspend fun get(id: ID): D?
    suspend fun remove(id: ID)
}