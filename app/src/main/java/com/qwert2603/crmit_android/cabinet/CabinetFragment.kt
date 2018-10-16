package com.qwert2603.crmit_android.cabinet

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.view.RxView
import com.qwert2603.andrlib.base.mvi.BaseFragment
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewAction
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.andrlib.util.showIfNotYet
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.KeyboardManager
import com.qwert2603.crmit_android.navigation.Screen
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.toolbar_default.*

class CabinetFragment : BaseFragment<CabinetViewState, CabinetView, CabinetPresenter>(), CabinetView {

    override fun createPresenter() = CabinetPresenter()

    override fun viewForSnackbar(): View? = cabinet_CoordinatorLayout

    private val logoutDialog by lazy {
        AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_logout)
                .setCancelable(false)
                .create()
    }

    private val lastLessonAdapter = LastLessonAdapter()

    private val retryRefreshSubject: PublishSubject<Any> = PublishSubject.create<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_cabinet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_cabinet)
        toolbar.setOnClickListener { nestedScrollView.smoothScrollTo(0, 0) }

        cabinet_SwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)

        lastLessons_RecyclerView.itemAnimator = null
        lastLessons_RecyclerView.adapter = lastLessonAdapter
        lastLessonAdapter.modelItemClicks
                .subscribe { DiHolder.router.navigateTo(Screen.LessonsInGroup(it.groupId, it.date)) }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        logoutDialog.dismiss()
        super.onDestroyView()
    }

    override fun load(): Observable<Any> = Observable.just(Any())

    override fun refresh(): Observable<Any> = Observable.merge(
            RxSwipeRefreshLayout.refreshes(cabinet_SwipeRefreshLayout),
            retryRefreshSubject
    )

    override fun retry(): Observable<Any> = RxView.clicks(view!!.findViewById(R.id.LR_Retry_Button))

    override fun logoutClicks() = RxView.clicks(logout_Button)

    override fun onFioClicks() = RxView.clicks(fio_LinearLayout)

    override fun render(vs: CabinetViewState) {
        super.render(vs)

        cabinet_ViewAnimator.showIfNotYet(when {
            vs.lrModel.loading -> 1
            vs.lrModel.loadingError != null -> 2
            else -> 0
        })

        cabinet_SwipeRefreshLayout.isEnabled = vs.lrModel.canRefresh
        cabinet_SwipeRefreshLayout.isRefreshing = vs.lrModel.refreshing

        vs.fio?.let { fio_TextView.text = it }
        vs.loginResult?.let {
            accountType_TextView.setText(it.accountType.displayNameRes)
            fio_TextView.transitionName = "entity_name_${it.detailsId}"
            lastLessons_TextView.setText(when (it.accountType) {
                AccountType.MASTER -> R.string.text_last_lessons
                AccountType.TEACHER -> R.string.text_your_last_lessons
            })
        }
        vs.lastLessons?.let {
            lastLessons_TextView.setVisible(it.isNotEmpty())
            lastLessonAdapter.adapterList = BaseRecyclerViewAdapter.AdapterList(it)
        }

        if (vs.isLogout) {
            (requireActivity() as KeyboardManager).hideKeyboard()
            logoutDialog.show()
        } else {
            logoutDialog.dismiss()
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is CabinetViewAction) {
            if (va is LRViewAction.RefreshingError) {
                Snackbar.make(cabinet_CoordinatorLayout, R.string.refreshing_error_text, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry_loading_text) { retryRefreshSubject.onNext(Any()) }
                        .show()
            }
            return
        }
        when (va) {
            CabinetViewAction.ShowingCachedData -> Snackbar.make(cabinet_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
            CabinetViewAction.MoveToLogin -> DiHolder.router.newRootScreen(Screen.Login)
            is CabinetViewAction.MoveToUserDetails -> {
                val detailsScreenKey = DetailsScreenKey(
                        entityId = va.detailsId,
                        entityName = fio_TextView.text.toString(),
                        entityNameTextView = fio_TextView,
                        entityNameStrike = false // user's account in cabinet is always enabled.
                )
                DiHolder.router.navigateTo(
                        when (va.accountType) {
                            AccountType.MASTER -> Screen.MasterDetails(detailsScreenKey)
                            AccountType.TEACHER -> Screen.TeacherDetails(detailsScreenKey)
                        }
                )
            }
        }.also { }
    }
}