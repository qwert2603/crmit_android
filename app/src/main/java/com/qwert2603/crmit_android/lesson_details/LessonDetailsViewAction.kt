package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class LessonDetailsViewAction : ViewAction {
    object ShowingCachedData : LessonDetailsViewAction()
    object ShowThereWillBeAttendingChangesCaching : LessonDetailsViewAction()
}