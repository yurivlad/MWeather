package com.yurivlad.multiweather.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 *
 */
interface DispatchersProvider {
    val mainDispatcher: CoroutineDispatcher
    val workerDispatcher: CoroutineDispatcher
}

object DispatchersProviderImpl : DispatchersProvider {
    override val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val workerDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
}

class AssignableDispatcher(private val dispatcher: CoroutineDispatcher) : DispatchersProvider {
    override val mainDispatcher: CoroutineDispatcher
        get() = dispatcher
    override val workerDispatcher: CoroutineDispatcher
        get() = dispatcher
}