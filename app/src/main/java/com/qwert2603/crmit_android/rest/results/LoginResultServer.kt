package com.qwert2603.crmit_android.rest.results

import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.LoginResult

data class LoginResultServer(
        val token: String,
        val accountType: AccountType,
        val detailsId: Long
) {
    fun toLoginResult() = LoginResult(accountType, detailsId)
}