package com.qwert2603.crmit_android.payments_in_group

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class PaymentsInGroupViewAction : ViewAction {
    object ShowingCachedData : PaymentsInGroupViewAction()
    object ShowThereWillBePaymentChangesCaching : PaymentsInGroupViewAction()
}