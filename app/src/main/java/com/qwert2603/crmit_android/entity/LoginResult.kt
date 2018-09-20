package com.qwert2603.crmit_android.entity

data class LoginResult(
        val token: String,
        val accountType: AccountType,
        val detailsId: Long
)