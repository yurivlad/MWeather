package com.yurivlad.multiweather.core

/**
 *
 */
interface StringsProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}