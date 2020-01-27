package com.yurivlad.multiweather.domainImpl

import com.yurivlad.multiweather.core.createCombinedErrorChannel
import com.yurivlad.multiweather.core.createCombinedProgressChannel
import com.yurivlad.multiweather.core.createCombinedValueChannel
import com.yurivlad.multiweather.domainModel.RepositoryDomain
import com.yurivlad.multiweather.domainModel.UseCaseBaseImpl
import com.yurivlad.multiweather.domainModel.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 *
 */

@ExperimentalCoroutinesApi
class WeatherSourcesForecastUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val gisRepo: RepositoryDomain<ForecastWithDayParts, Gis10DayForecastRequest>,
    private val yaRepo: RepositoryDomain<ForecastWithDayParts, Ya10DayForecastRequest>,
    private val primRepo: RepositoryDomain<ForecastWithDayParts, Prim7DayForecastRequest>
) :
    UseCaseBaseImpl<ForecastSources, NoParamsRequest>(coroutineDispatcher) {

    private val gisRequest = Gis10DayForecastRequest("weather-nakhodka-4879")
    private val primRequest = Prim7DayForecastRequest("nahodka")
    private val yaRequest = Ya10DayForecastRequest(42.824037, 132.892811)

    init {
        val channels = listOf(
            gisRepo.getReceiveChannel(gisRequest),
            yaRepo.getReceiveChannel(yaRequest),
            primRepo.getReceiveChannel(primRequest)
        )


        workerScope.launch {
            val receiveChannel = createCombinedProgressChannel(channels)

            fun getMergedProgress() = channels.any { it.progressOrNull == true }

            while (!receiveChannel.isClosedForReceive) {
                receiveChannel.receive()
                val newValue = getMergedProgress()
                val lastValue = channel.progressSendChannel.valueOrNull
                if (lastValue == null || lastValue != newValue) {
                    channel.progressSendChannel.send(newValue)
                }
            }
        }

        workerScope.launch {
            val receiveChannel = createCombinedErrorChannel(channels)

            fun getMergedError() = channels.map { it.errorOrNull }.firstOrNull { it != null }

            while (!receiveChannel.isClosedForReceive) {
                receiveChannel.receive()
                val newValue = getMergedError()
                val lastValue = channel.errorSendChannel.valueOrNull
                if (lastValue == null || lastValue != newValue) {
                    channel.errorSendChannel.send(newValue)
                }
            }
        }
        workerScope.launch {
            val receiveChannel = createCombinedValueChannel(channels)

            while (!receiveChannel.isClosedForReceive) {
                receiveChannel.receive()
                channel.valueSendChannel.send(
                    ForecastSources(
                        channels.mapNotNull {
                            it.valueOrNull
                        }
                    )
                )
            }
        }
    }

    override fun action(params: NoParamsRequest) {
        gisRepo.requestUpdate(gisRequest)
        primRepo.requestUpdate(primRequest)
        yaRepo.requestUpdate(yaRequest)
    }
}