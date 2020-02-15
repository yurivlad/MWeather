package com.yurivlad.multiweather.presenterCore

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

/**
 *
 */
interface DrawablesProvider {
    fun getDrawable(@DrawableRes resId: Int): Drawable
}