package com.yurivlad.multiweather.weeklyForecastImpl

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.yurivlad.multiweather.core.DispatchersProvider
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.domainPresenterMappersModel.WeeklyForecastMapper
import com.yurivlad.multiweather.presenterModel.ForecastWithThreeSourcesPresenterModel
import com.yurivlad.multiweather.presenterCore.CompositeLiveData
import com.yurivlad.multiweather.weeklyForecastModel.StatefulViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

/**
 *
 */

@ExperimentalCoroutinesApi
class WeeklyForecastViewModel constructor(
    private val useCase: UseCase<ForecastSources, NoParamsRequest>,
    private val toPresenterMapper: WeeklyForecastMapper,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel(), StatefulViewModel {

    val forecastsLiveData: CompositeLiveData<ForecastWithThreeSourcesPresenterModel> =
        CompositeLiveData(
            onValue = liveData {
                val valueSub = useCase.resultChannel.openValueSubscription()
                while (!valueSub.isClosedForReceive) {
                    emit(
                        withContext(viewModelScope.coroutineContext + dispatchersProvider.workerDispatcher) {
                            toPresenterMapper.convert(
                                valueSub.receive(),
                                NoAdditionalData
                            )
                        }
                    )
                }
            },
            onProgress = liveData {
                val progressSub = useCase.resultChannel.openProgressSubscription()
                while (!progressSub.isClosedForReceive) {
                    emit(progressSub.receive())
                }

            },
            onError = liveData {
                val errorSub = useCase.resultChannel.openErrorSubscription()
                while (!errorSub.isClosedForReceive) {
                    emit(errorSub.receive())
                }
            }
        )


    override fun onComponentStart(bundle: Bundle?) {
        useCase.action(NoParamsRequest)
    }


    fun requestUpdate() {
        useCase.action(NoParamsRequest)
    }

    override fun onSaveState(bundle: Bundle) {

    }

    override fun onComponentStop() {

    }
}