package com.qwert2603.crmit_android.student_details

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.qwert2603.andrlib.model.IdentifiableLong

sealed class StudentDetailsListItem : IdentifiableLong

data class StudentDetailsField(
        @StringRes val fieldTitleStringRes: Int,
        val fieldValue: String,
        @DrawableRes val iconDrawableRes: Int? = null
) : StudentDetailsListItem() {
    override val id = fieldTitleStringRes.toLong()
}

data class StudentDetailsSystemInfo(
        val enabled: Boolean,
        val filled: Boolean
) : StudentDetailsListItem() {
    override val id = 34537923963497L
}