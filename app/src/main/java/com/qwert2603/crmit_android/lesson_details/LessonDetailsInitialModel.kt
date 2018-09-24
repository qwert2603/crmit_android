package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.entity.GroupBrief

data class LessonDetailsInitialModel(
        val groupBrief: GroupBrief?,
        val date: String?,
        val attendings: List<Attending>
)