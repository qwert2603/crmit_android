package com.qwert2603.crmit_android.rest.results

import com.qwert2603.crmit_android.entity.Lesson

data class CabinetInfoResult(
        val lastLessons: List<Lesson>,
        val actualAppBuildCode: Int,
        val fio: String
)