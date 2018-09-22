package com.qwert2603.crmit_android.login

import com.qwert2603.andrlib.base.mvi.PartialChange

sealed class LoginPartialChange : PartialChange {
    data class LoginChanged(val login: String) : LoginPartialChange()
    data class PasswordChanged(val password: String) : LoginPartialChange()
    object LoggingStarted : LoginPartialChange()
    object LoggingError : LoginPartialChange()
    object LoggingSuccess : LoginPartialChange()
}