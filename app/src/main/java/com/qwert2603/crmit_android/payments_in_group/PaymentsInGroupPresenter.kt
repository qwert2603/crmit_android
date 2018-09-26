package com.qwert2603.crmit_android.payments_in_group

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.util.getMonthNumber
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

class PaymentsInGroupPresenter(private val groupId: Long)
    : LRPresenter<Any, GroupFull, PaymentsInGroupViewState, PaymentsInGroupView>(DiHolder.uiSchedulerProvider) {

    override val initialState = PaymentsInGroupViewState(EMPTY_LR_MODEL, null, null, null, 0)

    private val loadRefreshPartialChanges = loadRefreshPartialChanges().shareAfterViewSubscribed()

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges,
            loadIntent
                    .map { DiHolder.userSettingsRepo.loginResult }
                    .map { PaymentsInGroupPartialChange.AuthedUserLoaded(it) },
            intent { it.monthSelected() }
                    .map { PaymentsInGroupPartialChange.SelectedMonthChanged(it) }
    )

    private fun getGroupFromServer() = DiHolder.rest
            .getGroupDetails(groupId)
            .doOnSuccess { DiHolder.groupFullDaoInterface.saveItem(it) }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun initialModelSingle(additionalKey: Any): Single<GroupFull> = getGroupFromServer()
            .onErrorResumeNext { t ->
                LogUtils.e("PaymentsInGroupPresenter getGroupFromServer", t)
                val cached = DiHolder.groupFullDaoInterface.getItem(groupId)
                if (cached != null) {
                    viewActions.onNext(PaymentsInGroupViewAction.ShowingCachedData)
                    Single.just(cached)
                } else {
                    Single.error(Exception("no cache!"))
                }
            }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<GroupFull> = getGroupFromServer()

    override fun PaymentsInGroupViewState.applyInitialModel(i: GroupFull) = copy(groupFull = i)

    override fun stateReducer(vs: PaymentsInGroupViewState, change: PartialChange): PaymentsInGroupViewState {
        if (change !is PaymentsInGroupPartialChange) return super.stateReducer(vs, change)
                .let {
                    if (change is LRPartialChange.InitialModelLoaded<*> && vs.groupFull == null) {
                        val currentMonthNumber = Date().getMonthNumber()
                        it.groupFull!!
                        it.copy(
                                selectedMonth = when {
                                    currentMonthNumber < it.groupFull.startMonth -> it.groupFull.startMonth
                                    currentMonthNumber > it.groupFull.endMonth -> it.groupFull.endMonth
                                    else -> currentMonthNumber
                                }
                        )
                    } else {
                        it
                    }
                }
        return when (change) {
            is PaymentsInGroupPartialChange.AuthedUserLoaded -> vs.copy(
                    authedUserAccountType = change.loginResult.accountType,
                    authedUserDetailsId = change.loginResult.detailsId
            )
            is PaymentsInGroupPartialChange.SelectedMonthChanged -> vs.copy(selectedMonth = change.month)
        }
    }

    override fun bindIntents() {
        super.bindIntents()

        loadRefreshPartialChanges
                .filter { it is LRPartialChange.InitialModelLoaded<*> }
                .firstOrError()
                .delay(2, TimeUnit.SECONDS)
                .doOnSuccess {
                    if (!DiHolder.userSettingsRepo.thereWillBePaymentChangesCachingShown) {
                        DiHolder.userSettingsRepo.thereWillBePaymentChangesCachingShown = true
                        viewActions.onNext(PaymentsInGroupViewAction.ShowThereWillBePaymentChangesCaching)
                    }
                }
                .toObservable()
                .subscribeToView()
    }
}