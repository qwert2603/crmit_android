package com.qwert2603.crmit_android.payments_in_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.renderIfChanged
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.util.SaveImageLifecycleObserver
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_payments_in_group.*

@FragmentWithArgs
class PaymentsInGroupFragment : LRFragment<PaymentsInGroupViewState, PaymentsInGroupView, PaymentsInGroupPresenter>(), PaymentsInGroupView {

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    @Arg(required = false)
    var monthNumber: Int? = null

    override fun createPresenter() = PaymentsInGroupPresenter(groupId, monthNumber)

    override fun loadRefreshPanel(): LoadRefreshPanel = paymentsInGroup_LRPanelImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(SaveImageLifecycleObserver())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_payments_in_group)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_payments_in_group_default)
        months_TabLayout.setupWithViewPager(months_ViewPager)
        months_ViewPager.offscreenPageLimit = 12

        super.onViewCreated(view, savedInstanceState)
    }

    override fun monthSelected(): Observable<Int> = RxViewPager.pageSelections(months_ViewPager)
            .skipInitialValue()
            .map { it + (currentViewState.groupBrief?.startMonth ?: 0) }

    override fun render(vs: PaymentsInGroupViewState) {
        super.render(vs)

        renderIfChanged({ groupBrief }) { if (it != null) toolbar.title = getString(R.string.title_payments_in_group_format, it.name) }

        renderIfChanged({ groupBrief }) { groupBrief ->
            if (groupBrief != null) {
                months_ViewPager.adapter = MonthsAdapter(childFragmentManager, resources, groupBrief)
                months_ViewPager.currentItem = vs.selectedMonth - (vs.groupBrief?.startMonth ?: 0)
            } else {
                months_ViewPager.adapter = null
            }

            val show = groupBrief != null && vs.canUserSeePayments()
            months_TabLayout.setVisible(show)
            months_ViewPager.setVisible(show)
        }
        renderIfChanged({ selectedMonth }) {
            months_ViewPager.setCurrentItem(it - (vs.groupBrief?.startMonth ?: 0), false)
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is PaymentsInGroupViewAction) return super.executeAction(va)
        when (va) {
            PaymentsInGroupViewAction.ShowingCachedData -> Snackbar
                    .make(paymentsInGroup_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT)
                    .show()
            PaymentsInGroupViewAction.ShowThereWillBePaymentChangesCaching -> ThereWillBePaymentChangesCachingDialogFragment()
                    .show(requireFragmentManager(), null)
        }
    }
}