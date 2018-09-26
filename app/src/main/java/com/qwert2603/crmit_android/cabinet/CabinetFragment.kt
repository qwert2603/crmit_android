package com.qwert2603.crmit_android.cabinet

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.navigation.KeyboardManager
import com.qwert2603.crmit_android.navigation.ScreenKey
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.toolbar_default.*

class CabinetFragment : LRFragment<CabinetViewState, CabinetView, CabinetPresenter>(), CabinetView {

    override fun createPresenter() = CabinetPresenter()

    override fun loadRefreshPanel(): LoadRefreshPanel = cabinet_LRPanelImpl

    override fun viewForSnackbar(): View? = cabinet_CoordinatorLayout

    private val logoutDialog by lazy {
        AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_logout)
                .setCancelable(false)
                .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_cabinet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_cabinet)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        logoutDialog.dismiss()
        super.onDestroyView()
    }

    override fun logoutClicks() = RxView.clicks(logout_Button)

    override fun onFioClicks() = RxView.clicks(fio_LinearLayout)

    override fun render(vs: CabinetViewState) {
        super.render(vs)

        vs.fio?.let { fio_TextView.text = it }
        vs.accountType?.let { accountType_TextView.setText(it.displayNameRes) }
        vs.detailsId?.let { fio_TextView.transitionName = "entity_name_$it" }

        if (vs.isLogout) {
            (requireActivity() as KeyboardManager).hideKeyboard()
            logoutDialog.show()
        } else {
            logoutDialog.dismiss()
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is CabinetViewAction) return super.executeAction(va)
        when (va) {
            CabinetViewAction.ShowingCachedData -> Snackbar.make(cabinet_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
            CabinetViewAction.MoveToLogin -> DiHolder.router.newRootScreen(ScreenKey.LOGIN.name)
            is CabinetViewAction.MoveToUserDetails -> DiHolder.router.navigateTo(
                    when (va.accountType) {
                        AccountType.MASTER -> ScreenKey.MASTER_DETAILS
                        AccountType.TEACHER -> ScreenKey.TEACHER_DETAILS
                    }.name,
                    EntityDetailsFragment.Key(
                            entityId = va.detailsId,
                            entityName = fio_TextView.text.toString(),
                            entityNameTextView = fio_TextView,
                            entityNameStrike = false // user's account in cabinet is always enabled.
                    )
            )
        }.also { }
    }
}