package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.crmit_android.entity.AccountType

sealed class CabinetViewAction : ViewAction {
    object ShowingCachedData : CabinetViewAction()
    object ShowUpdateAvailable : CabinetViewAction()
    data class MoveToUserDetails(val accountType: AccountType, val detailsId: Long) : CabinetViewAction()
    object MoveToLogin : CabinetViewAction()
}