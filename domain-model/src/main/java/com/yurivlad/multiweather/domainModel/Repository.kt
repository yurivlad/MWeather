package com.yurivlad.multiweather.domainModel

import com.yurivlad.multiweather.core.CompositeReceiveChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
interface RepositoryDomain<S : DomainModel, R : RepositoryRequest> {

    fun getReceiveChannel(request: R): CompositeReceiveChannel<S>

    fun requestUpdate(request: R)
}