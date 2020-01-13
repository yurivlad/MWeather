package com.yurivlad.multiweather.presenterUtils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 *
 */
open class CompositeLiveData<T>(
    open val onValue: LiveData<T> = MutableLiveData(),
    open val onProgress: LiveData<Boolean> = MutableLiveData(),
    open val onError: LiveData<Exception?> = MutableLiveData()
)

