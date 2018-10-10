package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.entity.Teacher

data class LessonDetailsInitialModel(
        val groupBrief: GroupBrief?,
        val teacher: Teacher?,
        val date: String?,
        val attendings: List<Attending>
)