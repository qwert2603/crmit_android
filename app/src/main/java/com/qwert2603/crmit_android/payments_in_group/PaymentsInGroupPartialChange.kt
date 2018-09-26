package com.qwert2603.crmit_android.payments_in_group

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.entity.LoginResult

sealed class PaymentsInGroupPartialChange : PartialChange {
    data class AuthedUserLoaded(val loginResult: LoginResult) : PaymentsInGroupPartialChange()
    data class SelectedMonthChanged(val month: Int) : PaymentsInGroupPartialChange()
}