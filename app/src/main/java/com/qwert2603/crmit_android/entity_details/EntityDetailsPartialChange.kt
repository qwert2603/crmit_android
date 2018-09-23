package com.qwert2603.crmit_android.entity_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.entity.LoginResult

sealed class EntityDetailsPartialChange : PartialChange {
    data class AuthedUserLoaded(val loginResult: LoginResult) : EntityDetailsPartialChange()
}