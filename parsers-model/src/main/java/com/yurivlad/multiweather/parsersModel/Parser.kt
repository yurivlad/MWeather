package com.yurivlad.multiweather.parsersModel

/**
 *
 */
interface Parser<T> {
    fun parse(inputHtml: String): T
}