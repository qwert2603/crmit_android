package com.qwert2603.crmit_android.di

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.LoginResult
import com.qwert2603.crmit_android.util.PrefsBoolean
import com.qwert2603.crmit_android.util.PrefsLoginResultNullable

class UserSettingsRepo(appContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    var greetingShown by PrefsBoolean(prefs, "greetingShown")
    var loginResult by PrefsLoginResultNullable(prefs, "loginResult", Gson())

    init {
        loginResult = LoginResult("3ca9a70a-93ef-4032-ac0a-1a0135c5a8c9", AccountType.MASTER, 1)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}