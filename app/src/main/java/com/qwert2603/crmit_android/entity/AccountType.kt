package com.qwert2603.crmit_android.entity

import androidx.annotation.StringRes
import com.qwert2603.crmit_android.R

enum class AccountType(@StringRes val displayNameRes: Int) {
    MASTER(R.string.account_type_master),
    TEACHER(R.string.account_type_teacher),
    DEVELOPER(R.string.account_type_developer),
    BOT(R.string.account_type_bot),
}