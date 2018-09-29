package com.qwert2603.crmit_android.util

import android.animation.Animator
import android.content.res.Resources
import android.graphics.Paint
import android.support.annotation.MainThread
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.SystemUser
import com.qwert2603.crmit_android.rest.Rest
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


fun Int.toPointedString() = toLong().toPointedString()

fun Int.toMonthString(resources: Resources): String {
    val monthName = resources.getStringArray(R.array.month_names)[this % CrmitConst.MONTHS_PER_YEAR]
    return "${this / CrmitConst.MONTHS_PER_YEAR + CrmitConst.START_YEAR} $monthName"
}

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

fun <T, U> makePair() = BiFunction { t: T, u: U -> Pair(t, u) }
fun <T, U> secondOfTwo() = BiFunction { _: T, u: U -> u }

fun SystemUser.toLastSeenString(resources: Resources): String {
    if (lastSeenWhere == SystemUser.LAST_SEEN_REGISTRATION) return resources.getString(R.string.text_never)
    val millis = lastSeen + TimeZone.getDefault().getOffset(lastSeen)
    val date = millis.toDateString(resources)
    val time = SimpleDateFormat(resources.getString(R.string.date_pattern_last_seen_time), Locale.getDefault()).format(Date(millis))
    @Suppress("DEPRECATION")
    val where = if (lastSeenWhere == SystemUser.LAST_SEEN_ANDROID) Html.fromHtml(" &#128241;") else ""
    return "$date $time$where"
}

fun Long.toDateString(resources: Resources): String {
    val calendar = Calendar.getInstance().also { it.time = it.time.onlyDate() }
    val date = Calendar.getInstance().also { it.time = Date(this).onlyDate() }

    if (calendar == date) return resources.getString(R.string.today_text)

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    if (calendar == date) return resources.getString(R.string.yesterday_text)

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    if (calendar == date) return resources.getString(R.string.day_before_yesterday_text)

    calendar.add(Calendar.DAY_OF_YEAR, 3)
    if (calendar == date) return resources.getString(R.string.tomorrow_text)

    calendar.add(Calendar.DAY_OF_YEAR, 1)
    if (calendar == date) return resources.getString(R.string.day_after_tomorrow_text)

    return SimpleDateFormat(resources.getString(R.string.date_pattern_last_seen_date), Locale.getDefault()).format(Date(this))
}

fun Date.onlyDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

inline fun <T, R> Observable<T>.mapNotNull(crossinline mapper: (T) -> R?): Observable<R> = this
        .filter { mapper(it) != null }
        .map { mapper(it)!! }

fun Date.getMonthNumber() = Calendar.getInstance()
        .also { it.time = this }
        .let { CrmitConst.MONTHS_PER_YEAR * (it.get(Calendar.YEAR) - CrmitConst.START_YEAR) + it.get(Calendar.MONTH) }

fun EditText.doAfterTextChanged(action: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            action(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}