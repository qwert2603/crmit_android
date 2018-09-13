package com.qwert2603.crmit_android.student_details

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class StudentDetailsViewAction : ViewAction {
    object ShowingCachedData : StudentDetailsViewAction()
}