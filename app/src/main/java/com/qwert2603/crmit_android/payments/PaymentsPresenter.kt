package com.qwert2603.crmit_android.payments

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Payment
import com.qwert2603.crmit_android.entity.UploadStatus
import com.qwert2603.crmit_android.util.CrmitConst
import com.qwert2603.crmit_android.util.makePair
import com.qwert2603.crmit_android.util.mapNotNull
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class PaymentsPresenter(
        private val groupId: Long,
        private val monthNumber: Int
) : LRPresenter<Any, List<Payment>, PaymentsViewState, PaymentsView>(DiHolder.uiSchedulerProvider) {

    override val canRefreshAtAll = false

    override val initialState = PaymentsViewState(
            lrModel = EMPTY_LR_MODEL,
            monthNumber = monthNumber,
            payments = null,
            uploadingPaymentsStatuses = emptyMap(),
            authedUserAccountType = null,
            authedUserDetailsId = null
    )

    private sealed class OnePaymentPC {
        abstract val paymentId: Long

        data class ValueChanged(override val paymentId: Long, val value: Int) : OnePaymentPC()
        data class CommentChanged(override val paymentId: Long, val comment: String) : OnePaymentPC()
        data class IsCashChanged(override val paymentId: Long, val cash: Boolean) : OnePaymentPC()
        data class IsConfirmedChanged(override val paymentId: Long, val confirmed: Boolean) : OnePaymentPC()
    }

    private val paymentChanges: Observable<Payment> = Observable
            .merge(
                    intent { it.valueChanges() }.map { OnePaymentPC.ValueChanged(it.first, it.second) },
                    intent { it.commentChanges() }.map { OnePaymentPC.CommentChanged(it.first, it.second) },
                    intent { it.isCashChanges() }.map { OnePaymentPC.IsCashChanged(it.first, it.second) },
                    intent { it.isConfirmedChanges() }.map { OnePaymentPC.IsConfirmedChanged(it.first, it.second) }
            )
            .withLatestFrom(viewStateObservable, makePair())
            .mapNotNull { (change, vs) ->
                vs.payments
                        ?.singleOrNull { it.id == change.paymentId }
                        ?.let { it to change }

            }
            .map { (payment, change) ->
                when (change) {
                    is OnePaymentPC.ValueChanged -> payment.copy(value = change.value)
                    is OnePaymentPC.CommentChanged -> payment.copy(comment = change.comment)
                    is OnePaymentPC.IsCashChanged -> payment.copy(cash = change.cash)
                    is OnePaymentPC.IsConfirmedChanged -> payment.copy(confirmed = change.confirmed)
                }
            }
            .shareAfterViewSubscribed()

    private val savePaymentsScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges(),
            loadIntent
                    .switchMapSingle { DiHolder.userSettingsRepo.getLoginResultOrMoveToLogin() }
                    .map { PaymentsPartialChange.AuthedUserLoaded(it) },
            paymentChanges
                    .map { PaymentsPartialChange.PaymentChanged(it) },
            paymentChanges
                    .mergeWith(
                            intent { it.retryClicks() }
                                    .withLatestFrom(viewStateObservable, makePair())
                                    .mapNotNull { (paymentId, vs) ->
                                        vs.payments?.singleOrNull { it.id == paymentId }
                                    }
                    )
                    .flatMap { payment ->
                        DiHolder.rest.savePayment(payment.toSavePaymentParams())
                                .doOnComplete { DiHolder.paymentDao.saveItem(payment) }
                                .toSingleDefault<PaymentsPartialChange>(PaymentsPartialChange.UploadPaymentSuccess(payment.id))
                                .onErrorReturnItem(PaymentsPartialChange.UploadPaymentError(payment.id))
                                .toObservable()
                                .subscribeOn(savePaymentsScheduler)
                                .startWith(PaymentsPartialChange.UploadPaymentStarted(payment.id))
                                .takeUntil(paymentChanges.filter { it.id == payment.id })
                    }
    )

    override fun initialModelSingle(additionalKey: Any): Single<List<Payment>> = Single
            .fromCallable { DiHolder.paymentsInGroupInMonthDao.getPaymentsInGroupInMonth(groupId, monthNumber) }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun PaymentsViewState.applyInitialModel(i: List<Payment>) = copy(payments = i)

    override fun stateReducer(vs: PaymentsViewState, change: PartialChange): PaymentsViewState {
        if (change !is PaymentsPartialChange) return super.stateReducer(vs, change)
                .let { viewState ->
                    if (change is LRPartialChange.InitialModelLoaded<*>)
                        viewState.copy(uploadingPaymentsStatuses = emptyMap())
                    else
                        viewState
                }
        return when (change) {
            is PaymentsPartialChange.AuthedUserLoaded -> vs.copy(
                    authedUserAccountType = change.loginResult.accountType,
                    authedUserDetailsId = change.loginResult.detailsId
            )
            is PaymentsPartialChange.PaymentChanged -> vs.copy(
                    payments = vs.payments?.map {
                        if (it.id == change.payment.id)
                            change.payment
                        else
                            it
                    }
            )
            is PaymentsPartialChange.UploadPaymentStarted -> vs.copy(
                    uploadingPaymentsStatuses = vs.uploadingPaymentsStatuses + Pair(change.paymentId, UploadStatus.UPLOADING)
            )
            is PaymentsPartialChange.UploadPaymentError -> vs.copy(
                    uploadingPaymentsStatuses = vs.uploadingPaymentsStatuses + Pair(change.paymentId, UploadStatus.ERROR)
            )
            is PaymentsPartialChange.UploadPaymentSuccess -> vs.copy(
                    uploadingPaymentsStatuses = vs.uploadingPaymentsStatuses + Pair(change.paymentId, UploadStatus.DONE)
            )
        }
    }

    override fun bindIntents() {
        super.bindIntents()

        intent { it.askToEditValue() }
                .withLatestFrom(viewStateObservable, makePair())
                .mapNotNull { (paymentId, vs) ->
                    vs.payments?.singleOrNull { it.id == paymentId }
                }
                .doOnNext { payment ->
                    viewActions.onNext(PaymentsViewAction.AskToEditValue(
                            paymentId = payment.id,
                            value = payment.value,
                            maxValue = payment.needToPay,
                            title = payment.toDialogTitle()
                    ))
                }
                .subscribeToView()

        intent { it.askToEditComment() }
                .withLatestFrom(viewStateObservable, makePair())
                .mapNotNull { (paymentId, vs) ->
                    vs.payments?.singleOrNull { it.id == paymentId }
                }
                .doOnNext { payment ->
                    viewActions.onNext(PaymentsViewAction.AskToEditComment(
                            paymentId = payment.id,
                            comment = payment.comment,
                            title = payment.toDialogTitle()
                    ))
                }
                .subscribeToView()
    }

    companion object {
        private fun Int.toMonthTabTitle(): String {
            val monthName = DiHolder.resources.getStringArray(R.array.month_names)[this % CrmitConst.MONTHS_PER_YEAR]
            return "$monthName ${this / CrmitConst.MONTHS_PER_YEAR + CrmitConst.START_YEAR}"
        }

        private fun Payment.toDialogTitle() = "$studentFio\n$groupName ${monthNumber.toMonthTabTitle()}"
    }
}