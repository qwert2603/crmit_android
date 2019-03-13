package com.qwert2603.crmit_android.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager

/**
 * Save [Fragment]'s image and sets is as background.
 * Because in fragments those have [ViewPager] with nested fragments image disappears while exit animations.
 */
class SaveImageLifecycleObserver : LifecycleObserver {
    private var prevBackground: Drawable? = null
    private var everPaused = false

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(lifecycleOwner: LifecycleOwner) {
        val view: View = (lifecycleOwner as Fragment).view ?: return
        if (view.width == 0 || view.height == 0) return
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.width, view.height)
        view.draw(canvas)
        prevBackground = view.background
        everPaused = true
        view.background = BitmapDrawable(view.resources, bitmap)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(lifecycleOwner: LifecycleOwner) {
        if (everPaused) (lifecycleOwner as Fragment).view?.background = prevBackground
    }
}