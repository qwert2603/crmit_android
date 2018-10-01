package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class SystemUser(
        override val id: Long,
        val login: String,
        val lastSeen: String,
        val lastSeenWhere: Int,
        val systemRoleName: String,
        val enabled: Boolean
) : IdentifiableLong {
    companion object {
        const val LAST_SEEN_REGISTRATION = 1
        const val LAST_SEEN_WEB = 2
        const val LAST_SEEN_ANDROID = 3
    }
}