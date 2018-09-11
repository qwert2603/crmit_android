package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListPresenter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.model.pagination.Page
import com.qwert2603.andrlib.model.pagination.fixed_size.FixedSizePagesLoader
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.di.DiHolder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.CompletableSubject
import java.util.concurrent.TimeUnit

class EntitiesListPresenter<E : IdentifiableLong>(
        private val source: (offset: Int, count: Int, search: String) -> Single<List<E>>,
        pageSize: Int
) : ListPresenter<String, Page<E>, EntitiesListViewState<E>, EntitiesListView<E>, E>(DiHolder.uiSchedulerProvider) {

    private val paginator = FixedSizePagesLoader<E>(pageSize)

    override val initialState = EntitiesListViewState<E>(
            lrModel = EMPTY_LR_MODEL,
            listModel = EMPTY_LIST_MODEL,
            showingList = emptyList(),
            searchOpen = false,
            searchQuery = ""
    )

    // todo: move it to andrlib.
    private val viewStateWasSubscribed = CompletableSubject.create()

    private val closeSearchClicksIntent = intent { it.closeSearchClicks() }.share()

    private val searchQueryChanges: Observable<String> = Observable
            .merge(
                    closeSearchClicksIntent
                            .map { "" },
                    intent { it.searchQueryChanges() }
                            .debounce(300, TimeUnit.MILLISECONDS)
            )
            .startWith(initialState.searchQuery)
            .distinctUntilChanged()
            .delaySubscription(viewStateWasSubscribed.toObservable<Any>())
            .share()

    override val reloadIntent: Observable<Any> = Observable.merge(
            super.reloadIntent,
            searchQueryChanges.skip(1)
    )

    override fun EntitiesListViewState<E>.applyInitialModel(i: Page<E>) = copy(
            listModel = listModel.copy(allItemsLoaded = i.allItemsLoaded),
            showingList = i.list
    )

    override fun initialModelSingle(additionalKey: String): Single<Page<E>> = paginator.firstPage {
        source(it.offset, it.limit, additionalKey)
                .subscribeOn(DiHolder.modelSchedulersProvider.io)
    }

    override fun nextPageSingle(): Single<Page<E>> = paginator.nextPage()

    override fun EntitiesListViewState<E>.addNextPage(nextPage: List<E>): EntitiesListViewState<E> = copy(showingList = showingList + nextPage)

    override val partialChanges: Observable<PartialChange> = Observable.merge(listOf(
            loadRefreshPartialChanges(searchQueryChanges),
            paginationChanges(),
            intent { it.openSearchClicks() }
                    .map { EntitiesListPartialChange.OpenSearch },
            closeSearchClicksIntent
                    .map { EntitiesListPartialChange.CloseSearch },
            searchQueryChanges
                    .skip(1)
                    .map { EntitiesListPartialChange.SearchQueryChanged(it) }
    ))

    override fun bindIntents() {
        super.bindIntents()
        viewStateWasSubscribed.onComplete()
    }

    override fun stateReducer(vs: EntitiesListViewState<E>, change: PartialChange): EntitiesListViewState<E> {
        LogUtils.d("EntitiesListPresenter stateReducer $change")
        if (change !is EntitiesListPartialChange) return super.stateReducer(vs, change)
        return when (change) {
            EntitiesListPartialChange.OpenSearch -> vs.copy(searchOpen = true)
            EntitiesListPartialChange.CloseSearch -> vs.copy(searchOpen = false)
            is EntitiesListPartialChange.SearchQueryChanged -> vs.copy(searchQuery = change.query)
        }
    }
}