package com.yurivlad.multiweather.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel

/**
 *
 */

@ExperimentalCoroutinesApi
fun <T> CoroutineScope.createCombinedValueChannel(vararg channel: CompositeReceiveChannel<T>): ReceiveChannel<T> =
    combine(*channel
        .map { it.openValueSubscription() }
        .toTypedArray())
@ExperimentalCoroutinesApi
fun <T> CoroutineScope.createCombinedValueChannel(channels: List<CompositeReceiveChannel<T>>): ReceiveChannel<T> =
    combine(*channels.toTypedArray()
        .map { it.openValueSubscription() }
        .toTypedArray())

@ExperimentalCoroutinesApi
fun CoroutineScope.createCombinedErrorChannel(vararg channel: CompositeReceiveChannel<*>): ReceiveChannel<Exception?> =
    combine(*channel
        .map { it.openErrorSubscription() }
        .toTypedArray())

@ExperimentalCoroutinesApi
fun CoroutineScope.createCombinedErrorChannel(channels: List<CompositeReceiveChannel<*>>): ReceiveChannel<Exception?> =
    combine(*channels.toTypedArray()
        .map { it.openErrorSubscription() }
        .toTypedArray())


@ExperimentalCoroutinesApi
fun CoroutineScope.createCombinedProgressChannel(vararg channel: CompositeReceiveChannel<*>): ReceiveChannel<Boolean> =
    combine(*channel
        .map { it.openProgressSubscription() }
        .toTypedArray())

@ExperimentalCoroutinesApi
fun CoroutineScope.createCombinedProgressChannel(channels: List<CompositeReceiveChannel<*>>): ReceiveChannel<Boolean> =
    combine(*channels.toTypedArray()
        .map { it.openProgressSubscription() }
        .toTypedArray())
