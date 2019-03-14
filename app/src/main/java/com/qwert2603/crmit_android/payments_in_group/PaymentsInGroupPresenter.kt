package com.qwert2603.crmit_android.payments_in_group

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.payments.PaymentsPresenter
import com.qwert2603.crmit_android.util.NoCacheException
import com.qwert2603.crmit_android.util.getMonthNumber
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

class PaymentsInGroupPresenter(private val groupId: Long, private val monthNumber: Int?)
    : LRPresenter<Any, GroupBrief, PaymentsInGroupViewState, PaymentsInGroupView>(DiHolder.uiSchedulerProvider) {

    override val initialState = PaymentsInGroupViewState(EMPTY_LR_MODEL, null, null, null, 0)

    private val loadRefreshPartialChanges = loadRefreshPartialChanges().shareAfterViewSubscribed()

    private val paymentsDaoInterface = DiHolder.paymentDao.wrap(groupId)

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges,
            loadIntent
                    .switchMapSingle { DiHolder.userSettingsRepo.getLoginResultOrMoveToLogin() }
                    .map { PaymentsInGroupPartialChange.AuthedUserLoaded(it) },
            intent { it.monthSelected() }
                    .map { PaymentsInGroupPartialChange.SelectedMonthChanged(it) }
    )

    private fun getGroupFromServer() = DiHolder.rest
            .getPaymentsInGroup(groupId)
            .map { (group, payments) ->
                DiHolder.groupBriefCustomOrderDao.saveItem(group)

                /**
                 * Payments for this group are loaded only here.
                 * Save all payments for this group in DB.
                 * Later they will be loaded in [PaymentsPresenter].
                 */
                paymentsDaoInterface.deleteAllItems()
                paymentsDaoInterface.addItems(payments)
                group
            }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun initialModelSingle(additionalKey: Any): Single<GroupBrief> = getGroupFromServer()
            .onErrorResumeNext { t ->
                LogUtils.e("PaymentsInGroupPresenter getGroupFromServer", t)
                val cached = DiHolder.groupBriefCustomOrderDao.getItem(groupId)
                if (cached != null) {
                    viewActions.onNext(PaymentsInGroupViewAction.ShowingCachedData)
                    Single.just(cached)
                } else {
                    Single.error(NoCacheException())
                }
            }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<GroupBrief> = getGroupFromServer()

    override fun PaymentsInGroupViewState.applyInitialModel(i: GroupBrief) = copy(groupBrief = i)

    override fun stateReducer(vs: PaymentsInGroupViewState, change: PartialChange): PaymentsInGroupViewState {
        if (change !is PaymentsInGroupPartialChange) return super.stateReducer(vs, change)
                .let {
                    if (change is LRPartialChange.InitialModelLoaded<*> && vs.groupBrief == null) {
                        val monthNumber = monthNumber ?: Date().getMonthNumber()
                        it.groupBrief!!
                        it.copy(
                                selectedMonth = when {
                                    monthNumber < it.groupBrief.startMonth -> it.groupBrief.startMonth
                                    monthNumber > it.groupBrief.endMonth -> it.groupBrief.endMonth
                                    else -> monthNumber
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
                .delay(3, TimeUnit.SECONDS)
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