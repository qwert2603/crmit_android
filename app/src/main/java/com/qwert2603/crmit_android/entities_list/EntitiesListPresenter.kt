package com.qwert2603.crmit_android.entities_list

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.list.ListPresenter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.model.pagination.Page
import com.qwert2603.andrlib.model.pagination.fixed_size.FixedSizeKey
import com.qwert2603.andrlib.model.pagination.fixed_size.FixedSizePagesLoader
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.util.NoCacheException
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class EntitiesListPresenter<E : IdentifiableLong>(
        private val source: (offset: Int, count: Int, search: String) -> Single<List<E>>,
        private val dbDaoInterface: DaoInterface<E>,
        pageSize: Int
) : ListPresenter<String, Page<E>, EntitiesListViewState<E>, EntitiesListView<E>, E>(DiHolder.uiSchedulerProvider) {

    private val paginator = FixedSizePagesLoader<E>(pageSize)

    override val initialState = EntitiesListViewState<E>(
            lrModel = EMPTY_LR_MODEL,
            listModel = EMPTY_LIST_MODEL,
            showingList = emptyList(),
            searchOpen = false,
            searchQuery = "",
            authedUserAccountType = null,
            authedUserDetailsId = null
    )

    private val closeSearchClicksIntent = intent { it.closeSearchClicks() }.shareAfterViewSubscribed()

    private val searchQueryChanges: Observable<String> = Observable
            .merge(
                    closeSearchClicksIntent
                            .map { "" },
                    intent { it.searchQueryChanges() }
                            .debounce(300, TimeUnit.MILLISECONDS)
            )
            .startWith(initialState.searchQuery)
            .distinctUntilChanged()
            .shareAfterViewSubscribed()

    override val reloadIntent: Observable<Any> = Observable.merge(
            super.reloadIntent,
            searchQueryChanges.skip(1)
    )

    override fun EntitiesListViewState<E>.applyInitialModel(i: Page<E>) = copy(
            listModel = listModel.copy(allItemsLoaded = i.allItemsLoaded),
            showingList = i.list
    )

    override fun initialModelSingle(additionalKey: String): Single<Page<E>> = paginator.firstPage(
            object : Function1<FixedSizeKey, Single<List<E>>> {
                @Volatile
                private var fromServer = true

                override fun invoke(fixedSizeKey: FixedSizeKey): Single<List<E>> {
                    return if (fixedSizeKey.offset == 0) {
                        source(0, fixedSizeKey.limit, additionalKey)
                                .doOnSuccess {
                                    if (additionalKey.isEmpty()) {
                                        dbDaoInterface.deleteAllItems()
                                    }
                                    dbDaoInterface.addItems(it)
                                }
                                .onErrorResumeNext { t ->
                                    LogUtils.e("EntitiesListPresenter error from server!", t)
                                    fromServer = false
                                    Single
                                            .fromCallable { dbDaoInterface.getItems(additionalKey, 0, fixedSizeKey.limit) }
                                            .flatMap {
                                                if (it.isNotEmpty()) {
                                                    viewActions.onNext(EntitiesListViewAction.ShowingCachedData)
                                                    Single.just(it)
                                                } else {
                                                    Single.error(NoCacheException())
                                                }
                                            }

                                }
                    } else {
                        if (fromServer) {
                            source(fixedSizeKey.offset, fixedSizeKey.limit, additionalKey)
                                    .doOnSuccess { dbDaoInterface.addItems(it) }
                        } else {
                            Single.fromCallable { dbDaoInterface.getItems(additionalKey, fixedSizeKey.offset, fixedSizeKey.limit) }
                        }
                    }
                            .subscribeOn(DiHolder.modelSchedulersProvider.io)
                }
            }
    )

    override fun initialModelSingleRefresh(additionalKey: String): Single<Page<E>> = paginator.firstPage { (limit, offset) ->
        source(offset, limit, additionalKey)
                .doOnSuccess {
                    if (offset == 0 && additionalKey.isEmpty()) {
                        dbDaoInterface.deleteAllItems()
                    }
                    dbDaoInterface.addItems(it)
                }
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
                    .map { EntitiesListPartialChange.SearchQueryChanged(it) },
            loadIntent
                    .switchMapSingle { DiHolder.userSettingsRepo.getLoginResultOrMoveToLogin() }
                    .map { EntitiesListPartialChange.AuthedUserLoaded(it) }
    ))

    override fun stateReducer(vs: EntitiesListViewState<E>, change: PartialChange): EntitiesListViewState<E> {
        LogUtils.d { "EntitiesListPresenter stateReducer $change" }
        if (change !is EntitiesListPartialChange) return super.stateReducer(vs, change)
        return when (change) {
            is EntitiesListPartialChange.AuthedUserLoaded -> vs.copy(
                    authedUserAccountType = change.loginResult.accountType,
                    authedUserDetailsId = change.loginResult.detailsId
            )
            EntitiesListPartialChange.OpenSearch -> vs.copy(searchOpen = true)
            EntitiesListPartialChange.CloseSearch -> vs.copy(searchOpen = false)
            is EntitiesListPartialChange.SearchQueryChanged -> vs.copy(searchQuery = change.query)
        }
    }
}