package com.qwert2603.crmit_android.student_details

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.StudentFull

@GenerateLRChanger
data class StudentDetailsViewState(
        override val lrModel: LRModel,
        val studentFull: StudentFull?
) : LRViewState