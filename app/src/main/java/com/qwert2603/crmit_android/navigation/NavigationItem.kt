package com.qwert2603.crmit_android.navigation

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.qwert2603.andrlib.model.IdentifiableLong

data class NavigationItem(
        override val id: Long,
        @DrawableRes val iconRes: Int,
        @StringRes val titleRes: Int,
        val screenKey: ScreenKey
) : IdentifiableLong

