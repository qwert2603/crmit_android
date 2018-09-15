package com.qwert2603.crmit_android.entity_details

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class EntityDetailsViewAction : ViewAction {
    object ShowingCachedData : EntityDetailsViewAction()
}