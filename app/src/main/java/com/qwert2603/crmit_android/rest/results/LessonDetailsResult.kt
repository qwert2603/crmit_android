package com.qwert2603.crmit_android.rest.results

import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.entity.Lesson
import com.qwert2603.crmit_android.entity.Teacher

data class LessonDetailsResult(
        val group: GroupBrief,
        val teacher: Teacher,
        val lesson: Lesson,
        val attendings: List<Attending>
)