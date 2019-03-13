package com.qwert2603.crmit_android.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

interface ActivityInterface {
    val fragmentActivity: FragmentActivity
    val supportFragmentManager: FragmentManager
    val fragmentContainer: Int
    fun hideKeyboard()
    val navigationActivity: NavigationActivity
}