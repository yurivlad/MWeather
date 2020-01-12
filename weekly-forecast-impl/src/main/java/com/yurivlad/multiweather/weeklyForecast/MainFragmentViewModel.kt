package com.yurivlad.multiweather.weeklyForecast

import android.os.Bundle
import androidx.lifecycle.*
import com.yurivlad.multiweather.core.combine
import com.yurivlad.multiweather.domainModel.UseCase
import com.yurivlad.multiweather.domainModel.model.ForecastSources
import com.yurivlad.multiweather.domainModel.model.NoParamsRequest
import com.yurivlad.multiweather.domainPresenterMappersModel.NoAdditionalData
import com.yurivlad.multiweather.domainPresenterMappersModel.ToPresenterMapper
import com.yurivlad.multiweather.weeklyForecast.model.ForecastsData
import com.yurivlad.multiweather.presenterModel.ForecastWithThreeSourcesPresenterModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 *
 */

@ExperimentalCoroutinesApi
class MainFragmentViewModel constructor(private val useCase: UseCase<ForecastSources, NoParamsRequest>,
                                        private val toPreseneterMapper: ToPresenterMapper<ForecastSources, NoAdditionalData, ForecastWithThreeSourcesPresenterModel>
) :
    ViewModel(), StatefulViewModel {

    val forecastsLiveData: LiveData<ForecastsData> = Transformations
        .distinctUntilChanged(liveData {
            viewModelScope.launch {
                while (true) {
                    combine(
                        useCase.resultChannel.openProgressSubscription(),
                        useCase.resultChannel.openErrorSubscription(),
                        useCase.resultChannel.openValueSubscription()
                    ).receive()

                    val resultChannel = useCase.resultChannel
                    val sources = resultChannel.valueOrNull.orEmpty()


                    ForecastsData(
                        sources.getOrNull(0),
                        sources.getOrNull(1),
                        sources.getOrNull(2),
                        useCase.resultChannel.progressOrNull == true,
                        useCase.resultChannel.errorOrNull?.message
                    )
                }
            }
        })

    override fun onComponentStart(bundle: Bundle?) {
        val sources = useCase.resultChannel.valueOrNull
        if (sources.isNullOrEmpty()) useCase.action(NoParamsRequest)
    }

    fun requestUpdate() {
        useCase.action(NoParamsRequest)
    }

    override fun onSaveState(bundle: Bundle) {

    }

    override fun onComponentStop() {

    }
}