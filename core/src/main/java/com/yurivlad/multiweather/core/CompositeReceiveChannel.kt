package com.yurivlad.multiweather.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

/**
 *
 */

@ExperimentalCoroutinesApi
open class CompositeReceiveChannel<T> {
    protected val valueChannel = ConflatedBroadcastChannel<T>()
    protected val progressChannel = ConflatedBroadcastChannel<Boolean>()
    protected val errorChannel = ConflatedBroadcastChannel<Exception?>()

    val valueOrNull: T? get() = valueChannel.valueOrNull
    val progressOrNull: Boolean? get() = progressChannel.valueOrNull
    val errorOrNull: java.lang.Exception? get() = errorChannel.valueOrNull

    fun openValueSubscription(): ReceiveChannel<T> = valueChannel.openSubscription()
    fun openProgressSubscription(): ReceiveChannel<Boolean> = progressChannel.openSubscription()
    fun openErrorSubscription(): ReceiveChannel<Exception?> = errorChannel.openSubscription()
}


@ExperimentalCoroutinesApi
class CompositeBroadcastChannel<T> : CompositeReceiveChannel<T>() {
    val valueSendChannel: ConflatedBroadcastChannel<T> = valueChannel
    val progressSendChannel: ConflatedBroadcastChannel<Boolean> = progressChannel
    val errorSendChannel: ConflatedBroadcastChannel<Exception?> = errorChannel
}

@ExperimentalCoroutinesApi
fun <T> CoroutineScope.mergeChannels(vararg channels: ReceiveChannel<T>): ReceiveChannel<T> {
    return produce {
        while (true) {
            channels.forEach { channel ->
                while (!channel.isClosedForReceive) {
                    send(channel.receive())
                }
            }
        }
    }
}
