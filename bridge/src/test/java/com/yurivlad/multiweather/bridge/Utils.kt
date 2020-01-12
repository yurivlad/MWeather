package com.yurivlad.multiweather.bridge

import okhttp3.Dispatcher
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

/**
 *
 */
fun createTestDispatcher() = Dispatcher(object : AbstractExecutorService() {
    override fun isTerminated(): Boolean {
        return false
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    override fun shutdown() {

    }

    override fun shutdownNow(): MutableList<Runnable> {
        return arrayListOf()
    }

    override fun isShutdown(): Boolean {
        return false
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return true
    }
})