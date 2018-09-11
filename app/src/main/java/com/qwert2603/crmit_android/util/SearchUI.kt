package com.qwert2603.crmit_android.util

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.view.RxView
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.andrlib.util.toPx
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.navigation.KeyboardManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.view_search.view.*

class SearchUI(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var animator: Animator? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private val queryEditText by lazy { UserInputEditText(query_EditText) }

    init {
        this.inflate(R.layout.view_search, attachToRoot = true)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchUI)
        query_EditText.hint = typedArray.getString(R.styleable.SearchUI_android_hint)
        typedArray.recycle()

        clearSearch_Button.setOnClickListener { clearSearch() }
    }

    fun closeClicks() = RxView.clicks(closeSearch_Button)

    fun queryChanges(): Observable<String> = queryEditText.userInputs()

    fun setQuery(query: String) {
        queryEditText.setText(query)
    }

    fun isOpen() = search_FrameLayout.visibility == View.VISIBLE

    fun setOpen(open: Boolean, animate: Boolean) {
        if (animate) {
            if (open) {
                openSearchAnimate()
            } else {
                closeSearchAnimate()
            }
        } else {
            animator = null
            search_FrameLayout.setVisible(open)
        }
    }

    private fun openSearchAnimate() {
        if (isOpen()) return
        search_FrameLayout.setVisible(true)
        onPreDraw {
            (context as KeyboardManager).showKeyboard(query_EditText)
            val animator = ViewAnimationUtils
                    .createCircularReveal(
                            search_FrameLayout,
                            search_FrameLayout.width - search_FrameLayout.height / 2,
                            search_FrameLayout.height / 2,
                            resources.toPx(8).toFloat(),
                            search_FrameLayout.width.toFloat()
                    )
            this.animator = animator
            animator.start()
            false
        }
    }

    private fun closeSearchAnimate() {
        if (!isOpen()) return
        clearSearch()
        (context as KeyboardManager).hideKeyboard()
        val animator = ViewAnimationUtils
                .createCircularReveal(
                        search_FrameLayout,
                        search_FrameLayout.width - search_FrameLayout.height / 2,
                        search_FrameLayout.height / 2,
                        search_FrameLayout.width.toFloat(),
                        resources.toPx(8).toFloat()
                )
                .doOnEnd { search_FrameLayout.setVisible(false) }
        this.animator = animator
        animator.start()
    }

    private fun clearSearch() {
        query_EditText.setText("")
    }
}