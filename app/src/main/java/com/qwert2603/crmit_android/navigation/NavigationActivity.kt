package com.qwert2603.crmit_android.navigation

import androidx.fragment.app.Fragment

interface NavigationActivity {
    fun onFragmentResumed(fragment: Fragment)
    fun onFragmentPaused(fragment: Fragment)
}