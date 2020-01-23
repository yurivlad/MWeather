package com.yurivlad.multiweather.presenterUtils

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

/**
 *
 */
interface DrawblesProvider {
    fun getDrawable(@DrawableRes resId: Int): Drawable
}