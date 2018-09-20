package com.qwert2603.crmit_android.entity_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import io.reactivex.Observable
import io.reactivex.Single

class EntityDetailsPresenter<E : Any>(
        private val entityId: Long,
        private val source: (id: Long) -> Single<E>,
        private val dbDaoInterface: DaoInterface<E>
) : LRPresenter<Any, E, EntityDetailsViewState<E>, EntityDetailsView<E>>(DiHolder.uiSchedulerProvider) {
    override val initialState = EntityDetailsViewState<E>(EMPTY_LR_MODEL, null)

    override val partialChanges: Observable<PartialChange> = loadRefreshPartialChanges()

    override fun initialModelSingle(additionalKey: Any): Single<E> = source(entityId)
            .doOnSuccess { dbDaoInterface.saveItem(it) }
            .onErrorResumeNext { t ->
                LogUtils.e("EntityDetailsPresenter error from server!", t)
                val entity = dbDaoInterface.getItem(entityId)
                if (entity != null) {
                    viewActions.onNext(EntityDetailsViewAction.ShowingCachedData)
                    Single.just(entity)
                } else {
                    Single.error(Exception("no cache"))
                }
            }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun initialModelSingleRefresh(additionalKey: Any): Single<E> = source(entityId)
            .doOnSuccess { dbDaoInterface.saveItem(it) }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun EntityDetailsViewState<E>.applyInitialModel(i: E) = copy(entity = i)
}