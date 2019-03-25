package com.qwert2603.crmit_android.navigation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.util.*
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.dialogs.MarkInPlayMarketDialog
import com.qwert2603.crmit_android.dialogs.WhatsNewDialog
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.util.disposeOnDestroy
import com.qwert2603.crmit_android.util.subscribeWhileResumed
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_navigation.view.*
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationActivity, KeyboardManager, StatusBarActivity {

    companion object {

        private fun getScreenFromIntent(intent: Intent): Screen? {
            if (intent.action != Intent.ACTION_VIEW) return null
            val pathSegments = intent.data?.pathSegments ?: return null
            return when (pathSegments.getOrNull(0)) {
                "users" -> when (pathSegments.getOrNull(1)) {
                    "masters" -> Screen.Masters()
                    "teachers" -> Screen.Teachers()
                    "students" -> Screen.Students()
                    "master" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                                ?.let { Pair(ScreenKey.MASTER_DETAILS, it) } ?:*/ Screen.Masters()
                    "teacher" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                            ?.let { Pair(ScreenKey.TEACHER_DETAILS, it) } ?:*/ Screen.Teachers()
                    "student_details" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                            ?.let { Pair(ScreenKey.STUDENT_DETAILS, it) } ?:*/ Screen.Students()
                    else -> Screen.Cabinet()
                }
                "structure" -> when (pathSegments.getOrNull(1)) {
                    "groups" -> Screen.Groups()
                    "sections" -> Screen.Sections()
                    "group" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                            ?.let { Pair(ScreenKey.GROUP_DETAILS, it) } ?:*/ Screen.Groups()
                    "section" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                            ?.let { Pair(ScreenKey.SECTION_DETAILS, it) } ?:*/ Screen.Sections()
                    "group_details" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                            ?.let { Pair(ScreenKey.STUDENTS_IN_GROUP, it) } ?:*/ Screen.Groups()
                    "students_in_group" -> /*pathSegments.getOrNull(2)?.toIntOrNull()
                            ?.let { Pair(ScreenKey.STUDENTS_IN_GROUP, it) } ?:*/ Screen.Groups()
                    else -> Screen.Cabinet()
                }
//                "lessons" -> when {
//                    pathSegments.getOrNull(1) == "months" -> pathSegments.getOrNull(2)?.toIntOrNull()
//                            ?.let { Pair(ScreenKey.LESSONS_IN_GROUP, it) } ?: ScreenKey.CABINET
//                    else -> pathSegments.getOrNull(1)?.toIntOrNull()
//                            ?.let { Pair(ScreenKey.LESSONS_IN_GROUP, it) } ?: ScreenKey.GROUPS
//                }
//                "payment" -> {}
                else -> Screen.Cabinet()
            }
        }

        private fun getRootNavigationItems(accountType: AccountType?) =
                if (accountType != null) {
                    listOfNotNull(
                            NavigationItem(R.drawable.ic_business_center_black_24dp, R.string.title_cabinet, Screen.Cabinet()),
                            NavigationItem(R.drawable.ic_person_black_24dp, R.string.title_masters, Screen.Masters()),
                            NavigationItem(R.drawable.ic_person_black_24dp, R.string.title_teachers, Screen.Teachers()),
                            NavigationItem(R.drawable.ic_person_black_24dp, R.string.title_students, Screen.Students()),
                            NavigationItem(R.drawable.ic_person_black_24dp, R.string.title_developers, Screen.Developers()).takeIf { accountType == AccountType.DEVELOPER },
                            NavigationItem(R.drawable.ic_person_black_24dp, R.string.title_bots, Screen.Bots()).takeIf { accountType == AccountType.DEVELOPER },
                            NavigationItem(R.drawable.ic_group_black_24dp, R.string.title_sections, Screen.Sections()),
                            NavigationItem(R.drawable.ic_group_black_24dp, R.string.title_groups, Screen.Groups()),
                            NavigationItem(R.drawable.ic_schedule_black_24dp, R.string.title_last_seens, Screen.LastSeens()).takeIf { accountType == AccountType.DEVELOPER },
                            NavigationItem(R.drawable.ic_schedule_black_24dp, R.string.title_access_token, Screen.AccessTokens()).takeIf { accountType == AccountType.DEVELOPER },
                            NavigationItem(R.drawable.ic_date_range_black_24dp, R.string.title_schedule, Screen.Schedule(true)),
                            NavigationItem(R.drawable.ic_info_black_24dp, R.string.title_about, Screen.About())
                    )
                } else {
                    emptyList()
                }
    }

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

    private val navigator = Navigator(object : ActivityInterface {
        override val fragmentActivity = this@MainActivity
        override val supportFragmentManager = this@MainActivity.supportFragmentManager
        override val fragmentContainer = R.id.fragment_container
        override fun hideKeyboard() = this@MainActivity.hideKeyboard()
        override val navigationActivity = this@MainActivity
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LogUtils.d("MainActivity onCreate")

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val screenFromIntent = getScreenFromIntent(intent)
            when {
                screenFromIntent != null -> router.newRootScreen(screenFromIntent)
                !DiHolder.userSettingsRepo.greetingShown -> router.newRootScreen(Screen.Greeting())
                !DiHolder.userSettingsRepo.isLogged() -> router.newRootScreen(Screen.Login())
                else -> router.newRootScreen(Screen.Cabinet())
                        .also {
                            if (WhatsNewDialog.showIfNeeded(supportFragmentManager)) return@also
                            Single.timer(5, TimeUnit.SECONDS)
                                    .subscribe { _, t ->
                                        if (t == null) {
                                            MarkInPlayMarketDialog.showIfNeeded(supportFragmentManager)
                                        }
                                    }
                                    .disposeOnDestroy(this)
                        }
            }
        }

        headerNavigation = navigation_view.inflate(R.layout.header_navigation)
        navigation_view.addHeaderView(headerNavigation)
        headerNavigation.navigation_recyclerView.itemAnimator = null
        headerNavigation.navigation_recyclerView.adapter = navigationAdapter

        navigationAdapter.modelItemClicks
                .doOnNext {
                    navigationAdapter.selectedScreen = it.screen
                    navigateToItem(it, true)
                }
                .subscribeWhileResumed(this)

        navigationAdapter.modelItemLongClicks
                .doOnNext { navigateToItem(it, false) }
                .subscribeWhileResumed(this)

        DiHolder.userSettingsRepo.loginResult.changes
                .observeOn(DiHolder.uiSchedulerProvider.ui)
                .subscribe {
                    LogUtils.d("DiHolder.userSettingsRepo.loginResult.changes $it")
                    val navigationItems = getRootNavigationItems(it.t?.accountType)
                    navigationAdapter.adapterList = BaseRecyclerViewAdapter.AdapterList(navigationItems)
                }
                .disposeOnDestroy(this)

        lifecycle.addObserver(navigatorHolder.createLifecycleObserver(navigator))
    }

    override fun onStart() {
        super.onStart()
        activity_DrawerLayout.addDrawerListener(drawerListener)
    }

    override fun onStop() {
        activity_DrawerLayout.removeDrawerListener(drawerListener)
        super.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getScreenFromIntent(intent)
                ?.also {
                    closeDrawer()
                    DiHolder.router.newRootScreen(it)
                }
    }

    private fun navigateToItem(navigationItem: NavigationItem, newRootScreen: Boolean) {
        closeDrawer()
        if (navigationItem.screen is Screen.Schedule) {
            if (newRootScreen) {
                router.newRootScreen(Screen.Schedule(true))
            } else {
                router.navigateTo(Screen.Schedule(false))
            }
        } else {
            if (newRootScreen) {
                router.newRootScreen(navigationItem.screen)
            } else {
                router.navigateTo(navigationItem.screen)
            }
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

        val screen: Screen = fragment.getScreen() ?: return
        val isRoot = supportFragmentManager.backStackEntryCount == 0
        navigationAdapter.selectedScreen = if (isRoot) screen else null

        val allowDrawer = screen.allowDrawer && DiHolder.userSettingsRepo.isLogged()
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

    override fun setStatusBarBlack(black: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = if (black) 0 else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = resources.color(if (black) android.R.color.black else R.color.colorStatusBar)
        }
    }
}
