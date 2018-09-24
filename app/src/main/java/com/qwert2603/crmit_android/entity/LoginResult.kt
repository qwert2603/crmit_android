package com.qwert2603.crmit_android.entity

data class LoginResult(
        private val token: String,
        val accountType: AccountType,
        val detailsId: Long
) {
    fun getTokenSafe() = token

    override fun toString() = "LoginResult(token='<token>', accountType=$accountType, detailsId=$detailsId)"
}