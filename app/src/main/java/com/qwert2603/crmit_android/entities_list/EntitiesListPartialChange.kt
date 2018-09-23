package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.entity.LoginResult

sealed class EntitiesListPartialChange : PartialChange {
    data class AuthedUserLoaded(val loginResult: LoginResult) : EntitiesListPartialChange()
    object OpenSearch : EntitiesListPartialChange()
    object CloseSearch : EntitiesListPartialChange()
    data class SearchQueryChanged(val query: String) : EntitiesListPartialChange()
}