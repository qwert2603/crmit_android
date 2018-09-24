package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.entity.LoginResult

sealed class CabinetPartialChange : PartialChange {
    data class AuthedUserLoaded(val loginResult: LoginResult) : CabinetPartialChange()
    object LogoutStarted : CabinetPartialChange()
    object LogoutSuccess : CabinetPartialChange()
}