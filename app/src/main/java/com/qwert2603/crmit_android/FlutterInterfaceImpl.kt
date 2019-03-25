package com.qwert2603.crmit_android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.BotAccountIsNotSupportedException
import com.qwert2603.crmit_android.entity.StudentAccountIsNotSupportedException
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.MainActivity
import com.qwert2603.crmit_android.navigation.Screen
import io.flutter.plugins.FlutterInterface
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

object FlutterInterfaceImpl : FlutterInterface {

    @SuppressLint("StaticFieldLeak")
    private var resumedActivity: MainActivity? = null

    val cacheDirFile: File by lazy { File(CrmitApplication.APP_CONTEXT.cacheDir, "schedule_cache") }

    init {
        (CrmitApplication.APP_CONTEXT as Application)
                .registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityPaused(activity: Activity?) {
                        resumedActivity = null
                    }

                    override fun onActivityResumed(activity: Activity?) {
                        resumedActivity = activity as MainActivity
                    }

                    override fun onActivityStarted(activity: Activity?) {
                    }

                    override fun onActivityDestroyed(activity: Activity?) {
                    }

                    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                    }

                    override fun onActivityStopped(activity: Activity?) {
                    }

                    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                    }
                })
    }

    override fun getAuthedTeacherIdOrZero(): Long {
        val loginResult = DiHolder.userSettingsRepo.loginResult.field.t
        return when (loginResult?.accountType) {
            AccountType.MASTER -> 0
            AccountType.TEACHER -> loginResult.detailsId
            AccountType.DEVELOPER -> 0
            AccountType.BOT -> throw BotAccountIsNotSupportedException()
            AccountType.STUDENT -> throw StudentAccountIsNotSupportedException()
            null -> 0
        }
    }

    override fun getAccessToken(): String {
        val accessToken = DiHolder.userSettingsRepo.getAccessTokenSafe()
        if (accessToken != null) {
            return accessToken
        } else {
            DiHolder.userSettingsRepo.on401()
            throw Exception("DiHolder.userSettingsRepo.loginResult == null")
        }
    }

    override fun on401() {
        DiHolder.userSettingsRepo.clearAccessToken()
        DiHolder.userSettingsRepo.on401()
    }

    override fun navigateToGroup(groupId: Long, groupName: String) {
        DiHolder.router.navigateTo(Screen.GroupDetails(DetailsScreenKey(
                entityId = groupId,
                entityName = groupName
        )))
    }

    override fun getCacheDir(): String = cacheDirFile.path

    override fun onNavigationIconClicked() {
        val resumedActivity = resumedActivity
        if (resumedActivity != null) {
            if (resumedActivity.supportFragmentManager.backStackEntryCount == 0) {
                resumedActivity.hideKeyboard()
                resumedActivity.activity_DrawerLayout.openDrawer(GravityCompat.START)
            } else {
                DiHolder.router.exit()
            }
        } else {
            DiHolder.router.exit()
        }
    }
}