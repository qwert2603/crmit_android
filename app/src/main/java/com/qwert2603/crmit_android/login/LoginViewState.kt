package com.qwert2603.crmit_android.login

data class LoginViewState(
        val login: String,
        val password: String,
        val isLogging: Boolean
) {
    fun isLoginEnabled() = login.isNotEmpty() && password.isNotEmpty()
}