package com.qwert2603.crmit_android.di

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.qwert2603.crmit_android.util.PrefsBoolean
import com.qwert2603.crmit_android.util.PrefsLoginResultNullable
import com.qwert2603.crmit_android.util.PrefsStringNullable

class UserSettingsRepo(appContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    var greetingShown by PrefsBoolean(prefs, "greetingShown")
    var loginResult by PrefsLoginResultNullable(prefs, "loginResult", Gson())
    var displayFio by PrefsStringNullable(prefs, "displayFio")
    var thereWillBeAttendingChangesCachingShown by PrefsBoolean(prefs, "thereWillBeAttendingChangesCachingShown")

    fun isLogged() = loginResult != null

    @SuppressLint("ApplySharedPref")
    fun clear() {
        prefs.edit().clear().commit()
    }
}