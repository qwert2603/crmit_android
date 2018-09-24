package com.qwert2603.crmit_android.util

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.Attending
import kotlinx.android.synthetic.main.view_attending_state.view.*

class AttendingStateView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    companion object {
//        private const val ANIMATION_DURATION = 300L

//        fun View.getBackgroundColor() = (background as ColorDrawable).color
    }

    var state: Int = -1
        private set

//    private var backColorAnimator: Animator? = null
//        set(value) {
//            field?.cancel()
//            field = value
//            field?.start()
//        }

    var stateChangesFromClicksListener: ((Int) -> Unit)? = null

    init {
        inflate(R.layout.view_attending_state, attachToRoot = true)
        click_FrameLayout.setOnClickListener { stateChangesFromClicksListener?.invoke(nextState()) }
        if (isInEditMode) setAttendingState(Attending.ATTENDING_STATE_WAS_NOT_ILL)
    }

    fun setAttendingState(state: Int) {
        if (state == this.state) return
        this.state = state
        for (i in Attending.ATTENDING_STATES) {
//            backColor_FrameLayout.getChildAt(i)
//                    .animate()
//                    .cancel()

            backColor_FrameLayout.getChildAt(i).alpha = if (i == state) 1f else 0f
        }

//        backColorAnimator = null
        backColor_FrameLayout.setBackgroundColor(resources.color(when (state) {
            Attending.ATTENDING_STATE_WAS_NOT -> R.color.attending_state_was_not
            Attending.ATTENDING_STATE_WAS -> R.color.attending_state_was
            Attending.ATTENDING_STATE_WAS_NOT_ILL -> R.color.attending_state_was_not_ill
            else -> null!!
        }))
    }

    private fun nextState() = (state + 1) % Attending.ATTENDING_STATES.size

//    private fun setNextState() {
//        state = (state + 1) % Attending.ATTENDING_STATES.size
//        stateChangesFromClicksListener?.invoke(state)
//
//        for (i in Attending.ATTENDING_STATES) {
//            backColor_FrameLayout.getChildAt(i)
//                    .animate()
//                    .setDuration(ANIMATION_DURATION)
//                    .alpha(if (i == state) 1f else 0f)
//        }
//
//        backColorAnimator = ValueAnimator
//                .ofArgb(backColor_FrameLayout.getBackgroundColor(), resources.color(when (state) {
//                    Attending.ATTENDING_STATE_WAS_NOT -> R.color.attending_state_was_not
//                    Attending.ATTENDING_STATE_WAS -> R.color.attending_state_was
//                    Attending.ATTENDING_STATE_WAS_NOT_ILL -> R.color.attending_state_was_not_ill
//                    else -> null!!
//                }))
//                .setDuration(ANIMATION_DURATION)
//                .also {
//                    it.addUpdateListener { _ ->
//                        backColor_FrameLayout.setBackgroundColor(it.animatedValue as Int)
//                    }
//                }
//    }
}