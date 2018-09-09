package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.PartialChange

sealed class EntitiesListPartialChange : PartialChange {
    object OpenSearch : EntitiesListPartialChange()
    object CloseSearch : EntitiesListPartialChange()
    data class SearchQueryChanged(val query: String) : EntitiesListPartialChange()
}