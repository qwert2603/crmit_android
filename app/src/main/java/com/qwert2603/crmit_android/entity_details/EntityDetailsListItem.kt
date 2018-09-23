package com.qwert2603.crmit_android.entity_details

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.entity.GroupBrief

sealed class EntityDetailsListItem : IdentifiableLong

data class EntityDetailsField(
        @StringRes val fieldTitleStringRes: Int,
        val fieldValue: String,
        @DrawableRes val iconDrawableRes: Int? = null,
        @ColorRes val textColorRes: Int? = null,
        val clickCallback: (() -> Unit)? = null
) : EntityDetailsListItem() {
    override val id = fieldTitleStringRes.toLong()
}

data class EntityDetailsSystemInfo(
        val enabled: Boolean,
        val filled: Boolean
) : EntityDetailsListItem() {
    override val id = 34537923963497L
}

data class EntityDetailsGroupsList(
        val groups: List<GroupBrief>,
        val showTeacherFio: Boolean = true
) : EntityDetailsListItem() {
    override val id = 57028682756354L
}