package com.qwert2603.crmit_android.di

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.qwert2603.crmit_android.util.PrefsBoolean
import com.qwert2603.crmit_android.util.PrefsLoginResultNullable

class UserSettingsRepo(appContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    var greetingShown by PrefsBoolean(prefs, "greetingShown")
    var loginResult by PrefsLoginResultNullable(prefs, "loginResult", Gson())

    fun isLogged() = loginResult != null

    fun clear() {
        prefs.edit().clear().apply()
    }
}