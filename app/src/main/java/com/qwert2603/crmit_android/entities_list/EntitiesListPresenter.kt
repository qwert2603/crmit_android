package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListPresenter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.model.pagination.Page
import com.qwert2603.andrlib.model.pagination.fixed_size.FixedSizePagesLoader
import com.qwert2603.andrlib.schedulers.ModelSchedulersProvider
import com.qwert2603.andrlib.schedulers.UiSchedulerProvider
import com.qwert2603.andrlib.util.LogUtils
import io.reactivex.Observable
import io.reactivex.Single

class EntitiesListPresenter<E : IdentifiableLong>(
        uiSchedulerProvider: UiSchedulerProvider,
        private val modelSchedulersProvider: ModelSchedulersProvider,
        private val source: (offset: Int, count: Int, search: String) -> Single<List<E>>,
        pageSize: Int
) : ListPresenter<Any, Page<E>, EntitiesListViewState<E>, EntitiesListView<E>, E>(uiSchedulerProvider) {

    private val paginator = FixedSizePagesLoader<E>(pageSize)

    override val initialState = EntitiesListViewState<E>(EMPTY_LR_MODEL, EMPTY_LIST_MODEL, emptyList())

    override fun EntitiesListViewState<E>.applyInitialModel(i: Page<E>) = copy(
            listModel = listModel.copy(allItemsLoaded = i.allItemsLoaded),
            showingList = i.list
    )

    override fun initialModelSingle(additionalKey: Any): Single<Page<E>> = paginator.firstPage {
        source(it.offset, it.limit, "")
                .subscribeOn(modelSchedulersProvider.io)
    }

    override fun nextPageSingle(): Single<Page<E>> = paginator.nextPage()

    override fun EntitiesListViewState<E>.addNextPage(nextPage: List<E>): EntitiesListViewState<E> = copy(showingList = showingList + nextPage)

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges(),
            paginationChanges()
    )

    override fun stateReducer(vs: EntitiesListViewState<E>, change: PartialChange): EntitiesListViewState<E> {
        LogUtils.d("EntitiesListPresenter stateReducer $change")
        return super.stateReducer(vs, change)
    }
}