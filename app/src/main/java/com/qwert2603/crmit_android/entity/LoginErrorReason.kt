package com.qwert2603.crmit_android.entity

import androidx.annotation.StringRes
import com.qwert2603.crmit_android.R

class BotAccountIsNotSupportedException : Exception()
class StudentAccountIsNotSupportedException : Exception()

enum class LoginErrorReason(@StringRes val descriptionRes: Int) {
    WRONG_LOGIN_OR_PASSWORD(R.string.login_error_reason_wrong_login_or_password),
    ACCOUNT_DISABLED(R.string.login_error_reason_account_disabled),
    STUDENT_ACCOUNT_IN_NOT_SUPPORTED(R.string.login_error_reason_student_account_is_not_supported),
    BOT_ACCOUNT_IN_NOT_SUPPORTED(R.string.login_error_reason_bot_account_is_not_supported),
    ANOTHER(R.string.login_error_reason_another),
}