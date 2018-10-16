package com.qwert2603.crmit_android.lessons_in_group

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class LessonsInGroupViewAction : ViewAction {
    object ShowingCachedData : LessonsInGroupViewAction()
    object ShowThereWillBeAttendingChangesCaching : LessonsInGroupViewAction()
}