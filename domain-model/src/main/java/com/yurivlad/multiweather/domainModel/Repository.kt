package com.yurivlad.multiweather.domainModel

import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.core.CompositeReceiveChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import trikita.log.Log
import java.util.concurrent.ConcurrentHashMap

/**
 *
 */
@ExperimentalCoroutinesApi
interface RepositoryDomain<S : DomainModel, R : RepositoryRequest> {

    fun getModel(request: R): CompositeReceiveChannel<S>

    fun requestUpdate(request: R)
}

@ExperimentalCoroutinesApi
abstract class BaseRepositoryImpl<S : DomainModel, R : RepositoryRequest>(workerDispatcher: CoroutineDispatcher) :
    RepositoryDomain<S, R> {

    protected val channelsMap = ConcurrentHashMap<R, CompositeBroadcastChannel<S>>()
    protected val workerScope = CoroutineScope(workerDispatcher)

    override fun getModel(request: R): CompositeReceiveChannel<S> {
        return getOrCreateChannel(request)
    }

    protected fun getOrCreateChannel(request: R): CompositeBroadcastChannel<S> {
        return channelsMap.getOrPut(request) { CompositeBroadcastChannel() }
    }

    protected fun launchHandledCoroutine(
        usedChannel: CompositeBroadcastChannel<S>,
        block: suspend CoroutineScope.() -> Unit
    ) {

        usedChannel.progressSendChannel.offer(true)

        workerScope.launch {
            try {
                block()
                usedChannel.errorSendChannel.offer(null)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(e)
                usedChannel.errorSendChannel.offer(e)
            } finally {
                usedChannel.progressSendChannel.offer(false)
            }
        }
    }
}