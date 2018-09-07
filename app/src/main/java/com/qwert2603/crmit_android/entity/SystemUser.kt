package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class SystemUser(
        override val id: Long,
        val login: String,
        val lastSeen: Long,
        val system_role_name: String
): IdentifiableLong