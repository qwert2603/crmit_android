package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListView
import com.qwert2603.andrlib.model.IdentifiableLong
import io.reactivex.Observable

interface EntitiesListView<E : IdentifiableLong> : ListView<EntitiesListViewState<E>> {
    fun openSearchClicks(): Observable<Any>
    fun closeSearchClicks(): Observable<Any>
    fun searchQueryChanges(): Observable<String>
}