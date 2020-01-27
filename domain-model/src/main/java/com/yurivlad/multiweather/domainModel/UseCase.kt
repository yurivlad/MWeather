package com.yurivlad.multiweather.domainModel

import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.core.CompositeReceiveChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 *
 */
@ExperimentalCoroutinesApi
interface UseCase<T : DomainModel, R : UseCaseParams> {
    val resultChannel: CompositeReceiveChannel<T>
    fun action(params: R)
}

@ExperimentalCoroutinesApi
abstract class UseCaseBaseImpl<T : DomainModel, R : UseCaseParams>(
    workerDispatcher: CoroutineDispatcher
) : UseCase<T, R> {

    protected val workerScope = CoroutineScope(workerDispatcher)

    protected val channel = CompositeBroadcastChannel<T>()

    override val resultChannel: CompositeReceiveChannel<T> = channel
}