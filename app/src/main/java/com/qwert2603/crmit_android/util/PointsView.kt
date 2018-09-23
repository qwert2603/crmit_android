package com.qwert2603.crmit_android.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.toPx
import com.qwert2603.crmit_android.R

class PointsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    @ColorInt
    private val colorPrimary = resources.color(R.color.gray_point)
    @ColorInt
    private val colorAccent = resources.color(R.color.colorAccent)

    private val radius = resources.toPx(4).toFloat()

    private val interval = resources.toPx(16).toFloat()

    private val paint = Paint().also {
        it.style = Paint.Style.FILL
        it.isAntiAlias = true
    }

    private var current = 0
    private var count = 0

    init {
        if (isInEditMode) setProgress(2, 5)
    }

    fun setProgress(current: Int, count: Int) {
        this.current = current
        this.count = count
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val cx = canvas.width / 2f
        val cy = canvas.height / 2f

        for (i in 0 until count) {
            paint.color = if (i == current) colorAccent else colorPrimary
            val dx = i * interval - ((count - 1) * interval) / 2f
            canvas.drawCircle(cx + dx, cy, radius, paint)
        }
    }
}