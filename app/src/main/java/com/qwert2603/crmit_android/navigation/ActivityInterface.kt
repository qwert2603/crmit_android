package com.qwert2603.crmit_android.navigation

import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

interface ActivityInterface {
    val fragmentActivity: FragmentActivity
    val supportFragmentManager: FragmentManager
    val fragmentContainer: Int
    fun hideKeyboard()
    val navigationActivity: NavigationActivity
}