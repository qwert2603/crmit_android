package com.qwert2603.crmit_android.di

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.support.annotation.WorkerThread
import com.google.gson.Gson
import com.qwert2603.crmit_android.entity.LoginResult
import com.qwert2603.crmit_android.navigation.ScreenKey
import com.qwert2603.crmit_android.util.PrefsBoolean
import com.qwert2603.crmit_android.util.PrefsInt
import com.qwert2603.crmit_android.util.PrefsLoginResultNullable
import com.qwert2603.crmit_android.util.PrefsStringNullable
import io.reactivex.Single

class UserSettingsRepo(appContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    private var accessToken by PrefsStringNullable(prefs, "accessToken")

    var loginResult: LoginResult? by PrefsLoginResultNullable(prefs, "loginResult", Gson())

    var displayFio by PrefsStringNullable(prefs, "displayFio")

    var greetingShown by PrefsBoolean(prefs, "greetingShown")
    var thereWillBeAttendingChangesCachingShown by PrefsBoolean(prefs, "thereWillBeAttendingChangesCachingShown")
    var thereWillBePaymentChangesCachingShown by PrefsBoolean(prefs, "thereWillBePaymentChangesCachingShown")
    var whatsNewVersionCodeShown by PrefsInt(prefs, "whatsNewVersionCodeShown")

    fun isLogged() = accessToken != null

    fun getAccessTokenSafe() = accessToken

    fun saveAccessToken(token: String) {
        accessToken = token
    }

    fun clearAccessToken() {
        accessToken = null
    }

    fun getLoginResultOrMoveToLogin(): Single<LoginResult> = Single.create {
        val loginResult = loginResult
        if (!it.isDisposed && loginResult != null) {
            it.onSuccess(loginResult)
        } else {
            on401()
        }
    }

    fun on401() {
        DiHolder.uiSchedulerProvider.ui.scheduleDirect {
            DiHolder.router.newRootScreen(ScreenKey.LOGIN.name)
        }
    }

    fun clearUserInfo() {
        accessToken = null
        loginResult = null
        displayFio = null
    }

    @SuppressLint("ApplySharedPref")
    @WorkerThread
    fun clearAll() {
        prefs.edit().clear().commit()
    }
}