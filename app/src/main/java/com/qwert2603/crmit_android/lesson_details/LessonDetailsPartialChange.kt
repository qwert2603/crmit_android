package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.entity.LoginResult

sealed class LessonDetailsPartialChange : PartialChange {
    data class AuthedUserLoaded(val loginResult: LoginResult) : LessonDetailsPartialChange()
    data class AttendingStateChanged(val attendingId: Long, val state: Int) : LessonDetailsPartialChange()
    data class UploadAttendingStateStarted(val attendingId: Long) : LessonDetailsPartialChange()
    data class UploadAttendingStateError(val attendingId: Long) : LessonDetailsPartialChange()
    data class UploadAttendingStateSuccess(val attendingId: Long) : LessonDetailsPartialChange()
}