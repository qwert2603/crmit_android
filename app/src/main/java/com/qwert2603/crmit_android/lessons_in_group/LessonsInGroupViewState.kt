package com.qwert2603.crmit_android.lessons_in_group

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.Lesson

@GenerateLRChanger
data class LessonsInGroupViewState(
        override val lrModel: LRModel,
        val lessons: List<Lesson>?,
        val selectedDate: String
) : LRViewState {
    fun selectedIndex(): Int? = lessons?.indexOfFirst { it.date == selectedDate }
}