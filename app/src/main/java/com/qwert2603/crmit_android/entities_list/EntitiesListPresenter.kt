package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListPresenter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.model.pagination.Page
import com.qwert2603.andrlib.schedulers.ModelSchedulersProvider
import com.qwert2603.andrlib.schedulers.UiSchedulerProvider
import io.reactivex.Observable
import io.reactivex.Single

class EntitiesListPresenter<E : IdentifiableLong>(
        uiSchedulerProvider: UiSchedulerProvider,
        private val modelSchedulersProvider: ModelSchedulersProvider,
        private val source: Single<List<E>>
) : ListPresenter<Any, List<E>, EntitiesListViewState<E>, EntitiesListView<E>, E>(uiSchedulerProvider) {

    override val initialState = EntitiesListViewState<E>(EMPTY_LR_MODEL, EMPTY_LIST_MODEL_SINGLE_PAGE, emptyList())

    override fun EntitiesListViewState<E>.applyInitialModel(i: List<E>) = copy(showingList = i)

    override fun initialModelSingle(additionalKey: Any): Single<List<E>> = source.subscribeOn(modelSchedulersProvider.io)

    override fun nextPageSingle(): Single<Page<E>> = null!!

    override fun EntitiesListViewState<E>.addNextPage(nextPage: List<E>): EntitiesListViewState<E> = null!!

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges(),
            paginationChanges()
    )
}