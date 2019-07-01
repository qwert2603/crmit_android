package com.qwert2603.crmit_android.di

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.qwert2603.crmit_android.entity.LoginResult
import com.qwert2603.crmit_android.entity.Wrapper
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.*
import io.reactivex.Single

class UserSettingsRepo(appContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    private var accessToken by PrefsStringNullable(prefs, "accessToken")

    var loginResult: ObservableField<Wrapper<LoginResult>> = PreferenceUtils.createPrefsObjectObservable(
            prefs = prefs,
            key = "loginResult",
            gson = Gson(),
            defaultValue = Wrapper<LoginResult>(null)
    )

    var displayFio by PrefsStringNullable(prefs, "displayFio")

    var greetingShown by PrefsBoolean(prefs, "greetingShown")
    var thereWillBeAttendingChangesCachingShown by PrefsBoolean(prefs, "thereWillBeAttendingChangesCachingShown")
    var thereWillBePaymentChangesCachingShown by PrefsBoolean(prefs, "thereWillBePaymentChangesCachingShown")
    var whatsNewVersionCodeShown by PrefsInt(prefs, "whatsNewVersionCodeShown")
    var launchesCount by PrefsInt(prefs, "launchesCount_2")

    fun isLogged() = accessToken != null

    fun getAccessTokenSafe() = accessToken

    fun saveAccessToken(token: String) {
        accessToken = token
    }

    fun clearAccessToken() {
        accessToken = null
    }

    fun getLoginResultOrMoveToLogin(): Single<LoginResult> = Single.create {
        val loginResult = loginResult.field
        if (!it.isDisposed && loginResult.t != null) {
            it.onSuccess(loginResult.t)
        } else {
            DiHolder.on401()
        }
    }

    fun clearUserInfo() {
        accessToken = null
        loginResult.field = Wrapper(null)
        displayFio = null
    }

    @SuppressLint("ApplySharedPref")
    @WorkerThread
    fun clearAll() {
        prefs.edit().clear().commit()
    }
}