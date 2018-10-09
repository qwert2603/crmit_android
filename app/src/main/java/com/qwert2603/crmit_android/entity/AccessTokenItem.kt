package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class AccessTokenItem(
        override val id: Long,
        val expires: String,
        val systemUser: SystemUser,
        val fio: String
) : IdentifiableLong