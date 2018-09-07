package com.qwert2603.crmit_android.navigation

import android.support.v4.app.FragmentManager
import android.view.View

interface ActivityInterface {
    val supportFragmentManager: FragmentManager
    val fragmentContainer: Int
    fun finish()
    fun hideKeyboard()
    fun viewForSnackbars(): View
    val navigationActivity: NavigationActivity
}