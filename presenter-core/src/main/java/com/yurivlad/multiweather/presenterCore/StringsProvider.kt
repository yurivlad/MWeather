package com.yurivlad.multiweather.presenterCore

import androidx.annotation.StringRes

/**
 *
 */
interface StringsProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}