package com.qwert2603.crmit_android.util

import android.animation.Animator
import android.graphics.Paint
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import kotlin.math.absoluteValue

fun Int.toPointedString() = toLong().toPointedString()

fun Long.toPointedString(): String {
    val negative = this < 0
    val absString = this.absoluteValue.toString().reversed()
    val stringBuilder = StringBuilder()
    absString.forEachIndexed { index, c ->
        stringBuilder.append(c)
        if (index % 3 == 2 && index != absString.lastIndex) stringBuilder.append('.')
    }
    if (negative) stringBuilder.append('-')
    return stringBuilder.reverse().toString()
}

fun TextView.setStrike(strike: Boolean) {
    paintFlags = if (strike) {
        paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

fun Animator.doOnEnd(action: () -> Unit) = this.also {
    it.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            action()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    })
}

inline fun View.onPreDraw(crossinline action: () -> Boolean) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            return action()
        }
    })
}

fun EditText.setTextIfNotYet(text: String) {
    if (this.text.toString() != text) {
        val prevSelection = if (this.selectionStart == this.text.length) {
            text.length
        } else {
            this.selectionStart
        }

        this.setText(text)
        this.setSelection(prevSelection)
    }
}