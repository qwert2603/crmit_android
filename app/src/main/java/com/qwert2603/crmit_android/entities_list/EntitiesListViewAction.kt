package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class EntitiesListViewAction : ViewAction {
    object ShowingCachedData : EntitiesListViewAction()
}