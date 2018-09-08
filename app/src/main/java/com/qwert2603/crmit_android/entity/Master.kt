package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class Master(
        override val id: Long,
        val fio: String,
        val systemUser: SystemUser
) : IdentifiableLong