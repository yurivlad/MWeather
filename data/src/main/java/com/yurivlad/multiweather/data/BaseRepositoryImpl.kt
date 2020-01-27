package com.yurivlad.multiweather.data

import com.yurivlad.multiweather.core.CompositeBroadcastChannel
import com.yurivlad.multiweather.core.CompositeReceiveChannel
import com.yurivlad.multiweather.domainModel.DomainModel
import com.yurivlad.multiweather.domainModel.RepositoryDomain
import com.yurivlad.multiweather.domainModel.RepositoryRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import trikita.log.Log
import java.util.concurrent.ConcurrentHashMap

/**
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalCoroutinesApi
abstract class BaseRepositoryImpl<S : DomainModel, R : RepositoryRequest>(workerDispatcher: CoroutineDispatcher) :
    RepositoryDomain<S, R> {

    protected enum class SourceType {
        NETWORK, CACHE
    }

    protected val channelsMap = ConcurrentHashMap<R, CompositeBroadcastChannel<S>>()
    protected val workerScope = CoroutineScope(workerDispatcher)

    override fun getReceiveChannel(request: R): CompositeReceiveChannel<S> {
        return getOrCreateChannel(request)
    }

    protected fun getOrCreateChannel(request: R): CompositeBroadcastChannel<S> {
        return channelsMap.getOrPut(request) { CompositeBroadcastChannel() }
    }

    protected abstract suspend fun fetchModel(type: SourceType, request: R): S?

    protected open suspend fun onModelFetched(
        type: SourceType,
        value: S,
        channel: CompositeBroadcastChannel<S>
    ) {
        channel.valueSendChannel.offer(value)
    }

    protected open fun validateRequest(request: R): Boolean {
        return getOrCreateChannel(request).progressSendChannel.valueOrNull != true
    }


    override fun requestUpdate(request: R) {
        if (!validateRequest(request)) return

        val usedChannel = getOrCreateChannel(request)

        usedChannel.progressSendChannel.offer(true)

        workerScope.launch {
            try {
                if (usedChannel.valueSendChannel.valueOrNull == null) {
                    fetchModel(SourceType.CACHE, request)?.let { cachedValue ->
                        onModelFetched(SourceType.CACHE, cachedValue, usedChannel)
                    }
                }
                fetchModel(SourceType.NETWORK, request)?.let { cachedValue ->
                    onModelFetched(SourceType.NETWORK, cachedValue, usedChannel)
                }

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

