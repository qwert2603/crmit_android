package com.qwert2603.crmit_android.payments_in_group

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.util.SaveImageLifecycleObserver
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_payments_in_group.*

@FragmentWithArgs
class PaymentsInGroupFragment : LRFragment<PaymentsInGroupViewState, PaymentsInGroupView, PaymentsInGroupPresenter>(), PaymentsInGroupView {

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    override fun createPresenter() = PaymentsInGroupPresenter(groupId)

    override fun loadRefreshPanel(): LoadRefreshPanel = paymentsInGroup_LRPanelImpl

    override fun viewForSnackbar(): View? = paymentsInGroup_CoordinatorLayout

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
            .map { it + (currentViewState.groupFull?.startMonth ?: 0) }

    override fun render(vs: PaymentsInGroupViewState) {
        super.render(vs)

        renderIfChanged({ groupFull }) { if (it != null) toolbar.title = getString(R.string.title_payments_in_group_format, it.name) }

        renderIfChanged({ groupFull }) { groupFull ->
            if (groupFull != null) {
                val monthsAdapter = MonthsAdapter(childFragmentManager, resources, groupFull)
                months_ViewPager.adapter = monthsAdapter
                months_ViewPager.currentItem = vs.selectedMonth - (vs.groupFull?.startMonth ?: 0)
            } else {
                months_ViewPager.adapter = null
            }

            val show = groupFull != null && vs.canUserSeePayments()
            months_TabLayout.setVisible(show)
            months_ViewPager.setVisible(show)
        }
        renderIfChanged({ selectedMonth }) {
            months_ViewPager.setCurrentItem(it - (vs.groupFull?.startMonth ?: 0), false)
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is PaymentsInGroupViewAction) return super.executeAction(va)
        when (va) {
            PaymentsInGroupViewAction.ShowingCachedData -> Snackbar
                    .make(paymentsInGroup_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT)
                    .show()
            PaymentsInGroupViewAction.ShowThereWillBePaymentChangesCaching -> ThereWillBePaymentChangesCachingDialogFragment()
                    .show(fragmentManager, null)
        }
    }
}