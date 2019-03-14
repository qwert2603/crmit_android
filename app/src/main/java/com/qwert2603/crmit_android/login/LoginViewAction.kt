package com.qwert2603.crmit_android.login

import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.crmit_android.entity.LoginErrorReason

sealed class LoginViewAction : ViewAction {
    data class ShowLoginError(val loginErrorReason: LoginErrorReason) : LoginViewAction()
    object MoveToCabinet : LoginViewAction()
    object ShowUpdateAvailable : LoginViewAction()
}