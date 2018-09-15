package com.qwert2603.crmit_android.navigation

import android.app.Service
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.qwert2603.andrlib.base.mvi.BaseFragment
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.util.*
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_navigation.view.*
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class MainActivity : AppCompatActivity(), NavigationActivity, KeyboardManager {

    private val router: Router = DiHolder.router
    private val navigatorHolder: NavigatorHolder = DiHolder.navigatorHolder

    private val navigationAdapter = NavigationAdapter()

    private lateinit var headerNavigation: View

    private val drawerListener = object : DrawerLayout.SimpleDrawerListener() {
        override fun onDrawerStateChanged(newState: Int) {
            if (newState == DrawerLayout.STATE_DRAGGING) {
                hideKeyboard()
            }
        }
    }

    private val rootNavigationItems = listOf(
            NavigationItem(1L, R.drawable.ic_person_black_24dp, R.string.title_masters, ScreenKey.MASTERS),
            NavigationItem(2L, R.drawable.ic_person_black_24dp, R.string.title_teachers, ScreenKey.TEACHERS),
            NavigationItem(3L, R.drawable.ic_person_black_24dp, R.string.title_students, ScreenKey.STUDENTS),
            NavigationItem(4L, R.drawable.ic_group_black_24dp, R.string.title_sections, ScreenKey.SECTIONS),
            NavigationItem(5L, R.drawable.ic_group_black_24dp, R.string.title_groups, ScreenKey.GROUPS),
            NavigationItem(6L, R.drawable.ic_info_black_24dp, R.string.title_about, ScreenKey.ABOUT)
    )

    private val navigator = Navigator(object : ActivityInterface {
        override val supportFragmentManager = this@MainActivity.supportFragmentManager
        override val fragmentContainer = R.id.fragment_container
        override fun finish() = this@MainActivity.finish()
        override fun hideKeyboard() = this@MainActivity.hideKeyboard()
        override fun viewForSnackbars(): View = (supportFragmentManager.findFragmentById(R.id.fragment_container) as? BaseFragment<*, *, *>)
                ?.viewForSnackbar()
                ?: activity_CoordinatorLayout

        override val navigationActivity = this@MainActivity
    })

    private val navigationDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            router.newRootScreen(ScreenKey.MASTERS.name)
        }

        headerNavigation = navigation_view.inflate(R.layout.header_navigation)
        navigation_view.addHeaderView(headerNavigation)
        headerNavigation.navigation_recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        lifecycle.addObserver(navigatorHolder.createLifecycleObserver(navigator))
    }

    override fun onStart() {
        super.onStart()

        navigationAdapter.modelItemClicks
                .subscribe { navigateToItem(it, true) }
                .addTo(navigationDisposable)

        navigationAdapter.modelItemLongClicks
                .subscribe { navigateToItem(it, false) }
                .addTo(navigationDisposable)

        headerNavigation.navigation_recyclerView.adapter = navigationAdapter

        if (navigationAdapter.adapterList.modelList.isEmpty()) {
            navigationAdapter.adapterList = BaseRecyclerViewAdapter.AdapterList(rootNavigationItems)
        }

        activity_DrawerLayout.addDrawerListener(drawerListener)
    }

    override fun onStop() {
        with(headerNavigation) {
            navigation_recyclerView.adapter = null
        }
        activity_DrawerLayout.removeDrawerListener(drawerListener)
        navigationDisposable.clear()
        super.onStop()
    }

    private fun navigateToItem(navigationItem: NavigationItem, newRootScreen: Boolean) {
        closeDrawer()
        if (newRootScreen) {
            router.newRootScreen(navigationItem.screenKey.name)
        } else {
            router.navigateTo(navigationItem.screenKey.name)
        }
    }

    override fun onBackPressed() {
        if (closeDrawer()) return
        val resumedFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if ((resumedFragment as? BackPressListener)?.onBackPressed() == true) return
        router.exit()
    }


    override fun onFragmentResumed(fragment: Fragment) {
        LogUtils.d("onFragmentResumed $fragment")
        if (fragment !in supportFragmentManager.fragments) return

        val screenKey: ScreenKey = fragment.getScreenKey() ?: return
        val isRoot = supportFragmentManager.backStackEntryCount == 0
        if (isRoot) {
            navigationAdapter.selectedItemId = rootNavigationItems.find { screenKey == it.screenKey }?.id ?: 0
        } else {
            navigationAdapter.selectedItemId = 0
        }
        val allowDrawer = screenKey.allowDrawer
        activity_DrawerLayout.setDrawerLockMode(
                (if (allowDrawer) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED),
                GravityCompat.START
        )
        fragment.view
                ?.findViewById<Toolbar>(R.id.toolbar)
                ?.apply {
                    setSupportActionBar(this)
                    if (allowDrawer || !isRoot) {
                        navigationIcon = resources.drawable(when {
                            isRoot -> R.drawable.ic_menu_24dp
                            else -> R.drawable.ic_arrow_back_24dp
                        })
                        setNavigationOnClickListener {
                            if (isRoot) {
                                hideKeyboard()
                                activity_DrawerLayout.openDrawer(GravityCompat.START)
                            } else {
                                router.exit()
                            }
                        }
                    } else {
                        navigationIcon = null
                    }
                }
    }

    override fun onFragmentPaused(fragment: Fragment) {
        fragment.view
                ?.findViewById<Toolbar>(R.id.toolbar)
                ?.setNavigationOnClickListener(null)
    }

    private fun closeDrawer(): Boolean = activity_DrawerLayout.isDrawerOpen(GravityCompat.START)
            .also { if (it) activity_DrawerLayout.closeDrawer(GravityCompat.START) }

    override fun hideKeyboard(removeFocus: Boolean) {
        if (removeFocus) {
            activityRoot_FrameLayout.requestFocus()
        }
        val currentFocus = currentFocus ?: return
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }

    override fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        (getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, 0)
    }

    override fun isKeyBoardShown() = activityRoot_FrameLayout.height < resources.displayMetrics.heightPixels - resources.toPx(30)
}
