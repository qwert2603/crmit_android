package com.qwert2603.crmit_android.student_details

import android.widget.TextView

data class StudentDetailsKey(
        val studentId: Long,
        val studentFio: String,
        val systemUserEnabled: Boolean,
        val studentFioTextView: TextView
)