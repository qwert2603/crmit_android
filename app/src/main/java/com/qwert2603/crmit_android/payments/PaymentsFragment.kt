package com.qwert2603.crmit_android.payments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.page_list_item.AllItemsLoaded
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.navigation.ScreenKey
import com.qwert2603.crmit_android.util.ConditionDividerDecoration
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_payments.*

@FragmentWithArgs
class PaymentsFragment : LRFragment<PaymentsViewState, PaymentsView, PaymentsPresenter>(), PaymentsView {

    companion object {
        private const val REQUEST_CODE_VALUE = 1
        private const val REQUEST_CODE_COMMENT = 2
    }

    data class Key(val groupId: Long, val monthNumber: Int)

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    @Arg
    var monthNumber: Int = -1

    override fun createPresenter() = PaymentsPresenter(groupId, monthNumber)

    override fun loadRefreshPanel(): LoadRefreshPanel = payments_LRPanelImpl

    override fun viewForSnackbar(): View? = payments_LRPanelImpl

    private val adapter = PaymentsAdapter()

    private val valueChanges = PublishSubject.create<Pair<Long, Int>>()
    private val commentChanges = PublishSubject.create<Pair<Long, String>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_payments)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        payments_RecyclerView.adapter = adapter
        payments_RecyclerView.itemAnimator = null
        payments_RecyclerView.addItemDecoration(ConditionDividerDecoration(requireContext()) { _, vh -> vh is PaymentViewHolder })

        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(ScreenKey.STUDENT_DETAILS.name, EntityDetailsFragment.Key(
                            entityId = it.studentId,
                            entityName = it.studentFio
                    ))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return
        when (requestCode) {
            REQUEST_CODE_VALUE -> valueChanges.onNext(Pair(
                    data.getLongExtra(EditValueDialogFragment.KEY_PAYMENT_ID, 0L),
                    data.getIntExtra(EditValueDialogFragment.KEY_PAYMENT_VALUE, 0)
            ))
            REQUEST_CODE_COMMENT -> commentChanges.onNext(Pair(
                    data.getLongExtra(EditCommentDialogFragment.KEY_PAYMENT_ID, 0L),
                    data.getStringExtra(EditCommentDialogFragment.KEY_PAYMENT_COMMENT) ?: ""
            ))
        }
    }

    override fun isCashChanges(): Observable<Pair<Long, Boolean>> = adapter.isCashChanges
    override fun isConfirmedChanges(): Observable<Pair<Long, Boolean>> = adapter.isConfirmedChanges

    override fun askToEditValue(): Observable<Pair<Long, Int>> = adapter.askToEditValue
    override fun askToEditComment(): Observable<Pair<Long, String>> = adapter.askToEditComment

    override fun valueChanges(): Observable<Pair<Long, Int>> = valueChanges
    override fun commentChanges(): Observable<Pair<Long, String>> = commentChanges

    override fun retryClicks(): Observable<Long> = adapter.retryClicks

    override fun render(vs: PaymentsViewState) {
        super.render(vs)

        adapter.userCanConfirm = vs.isUserCanConfirm()
        adapter.uploadStatuses = vs.uploadingPaymentsStatuses
        val modelList = vs.payments ?: emptyList()
        adapter.adapterList = BaseRecyclerViewAdapter.AdapterList(modelList, AllItemsLoaded(modelList.size))
    }

    override fun executeAction(va: ViewAction) {
        if (va !is PaymentsViewAction) return super.executeAction(va)
        when (va) {
            is PaymentsViewAction.AskToEditValue -> EditValueDialogFragmentBuilder
                    .newEditValueDialogFragment(va.maxValue, va.paymentId, va.value)
                    .also { it.setTargetFragment(this, REQUEST_CODE_VALUE) }
                    .show(fragmentManager, null)
            is PaymentsViewAction.AskToEditComment -> EditCommentDialogFragmentBuilder
                    .newEditCommentDialogFragment(va.comment, va.paymentId)
                    .also { it.setTargetFragment(this, REQUEST_CODE_COMMENT) }
                    .show(fragmentManager, null)
        }.also { }
    }
}