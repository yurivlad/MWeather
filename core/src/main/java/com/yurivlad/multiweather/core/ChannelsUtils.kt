package com.yurivlad.multiweather.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select

/**
 *
 */
@UseExperimental(ExperimentalCoroutinesApi::class)
fun <T> CoroutineScope.combine(vararg channels: ReceiveChannel<T>): ReceiveChannel<T> = produce {
    while (!channels.any { it.isClosedForReceive }) {
        val value = select<T> {
            channels
                .filter { !it.isClosedForReceive }
                .forEach { channel -> channel.onReceive { it } }
        }
        offer(value)
    }
}