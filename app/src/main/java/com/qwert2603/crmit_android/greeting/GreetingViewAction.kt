package com.qwert2603.crmit_android.greeting

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class GreetingViewAction : ViewAction {
    object MoveToLogin : GreetingViewAction()
}