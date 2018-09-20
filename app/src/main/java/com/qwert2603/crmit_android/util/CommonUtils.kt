package com.qwert2603.crmit_android.util

import android.animation.Animator
import android.content.res.Resources
import android.graphics.Paint
import android.support.annotation.MainThread
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.rest.Rest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

fun Int.toPointedString() = toLong().toPointedString()

fun Int.toMonthString(resources: Resources) = "${this / 12 + 2017} ${resources.getStringArray(R.array.month_names)[this % 12]}"

private object DateFormats {
    val DATE_FORMAT_SERVER = SimpleDateFormat(Rest.DATE_FORMAT, Locale.getDefault())
    val DATE_FORMAT_SHOWING = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
}

@MainThread
fun String.toShowingDate(): String = this
        .let { DateFormats.DATE_FORMAT_SERVER.parse(it) }
        .let { DateFormats.DATE_FORMAT_SHOWING.format(it) }

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

