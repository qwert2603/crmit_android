package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class SystemUser(
        override val id: Long,
        val login: String,
        val lastSeen: Long,
        val systemRoleName: String,
        val enabled: Boolean
) : IdentifiableLong