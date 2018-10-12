package com.qwert2603.crmit_android.cabinet

import com.qwert2603.crmit_android.entity.Lesson

data class CabinetInitialModel(
        val fio: String,
        val lastLessons: List<Lesson>
)