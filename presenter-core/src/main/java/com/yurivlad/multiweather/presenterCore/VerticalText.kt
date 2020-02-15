package com.yurivlad.multiweather.presenterCore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat

/**
 *
 */
class VerticalText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultResId: Int = 0
) : View(context, attributeSet, defaultResId) {

    private val textSizeDip =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics)

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(this@VerticalText.context, R.color.textPrimary)
        density = resources.displayMetrics.density
        textSize = textSizeDip
    }

    var text: String = "Stub"
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        val temp = text
        canvas.rotate(-90f, measuredWidth / 2f, measuredHeight / 2f)
        canvas.drawText(temp, -paint.textSize / 2, height / 2.toFloat() + paint.textSize / 2, paint)
    }
}