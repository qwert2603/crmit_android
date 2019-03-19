package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class LastSeenItem(
        val systemUser: SystemUser,
        val fio: String,
        val detailsId: Long
) : IdentifiableLong {
    override val id: Long
        get() = systemUser.id
}