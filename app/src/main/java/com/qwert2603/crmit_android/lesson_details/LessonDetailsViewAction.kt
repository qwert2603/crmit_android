package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class LessonDetailsViewAction : ViewAction {
    data class NavigateToPayments(val groupId: Long, val monthNumber: Int) : LessonDetailsViewAction()
}