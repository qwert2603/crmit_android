package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.PartialChange

sealed class CabinetPartialChange : PartialChange {
    object LogoutStarted : CabinetPartialChange()
    object LogoutSuccess : CabinetPartialChange()
}