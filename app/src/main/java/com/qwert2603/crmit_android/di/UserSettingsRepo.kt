package com.qwert2603.crmit_android.di

import android.content.Context
import android.preference.PreferenceManager
import com.qwert2603.crmit_android.util.PrefsStringNullable

class UserSettingsRepo(appContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    var accessToken by PrefsStringNullable(prefs, "accessToken")

    init {
        accessToken = null//"e9a16a45-0e90-45cd-83a6-92db0d2c5304"
    }
}