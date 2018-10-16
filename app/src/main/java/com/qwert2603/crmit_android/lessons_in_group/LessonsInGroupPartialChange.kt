package com.qwert2603.crmit_android.lessons_in_group

import com.qwert2603.andrlib.base.mvi.PartialChange

sealed class LessonsInGroupPartialChange : PartialChange {
    data class SelectedDateChanged(val date: String) : LessonsInGroupPartialChange()
}