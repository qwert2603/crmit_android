package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class AccessTokensItem(
        val tokens: List<Token>,
        val systemUser: SystemUser,
        val fio: String,
        val detailsId: Long
) : IdentifiableLong {
    override val id: Long
        get() = systemUser.id

    data class Token(
            val appVersion: String,
            val device: String,
            val expires: String,
            val lastUse: String
    )
}