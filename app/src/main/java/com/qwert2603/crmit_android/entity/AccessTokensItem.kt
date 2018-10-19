package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class AccessTokensItem(
        val expiresList: List<String>,
        val systemUser: SystemUser,
        val fio: String
) : IdentifiableLong {
    override val id: Long
        get() = systemUser.id
}