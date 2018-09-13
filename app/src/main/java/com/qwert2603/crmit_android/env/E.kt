package com.qwert2603.crmit_android.env

import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.BuildConfig

object E {
    val env: EnvInterface = when (BuildConfig.FLAVOR) {
        "home" -> Home
        "prod" -> Prod
        else -> null!!
    }
}


abstract class EnvInterface {
    protected abstract val serverUrl: String
    val restBaseUrl by lazy { "$serverUrl/api/v1.0/" }
    abstract val logType: LogUtils.LogType
    abstract val showClearCacheButton: Boolean
}

private object Home : EnvInterface() {
    override val serverUrl = "http://192.168.1.26:1918"
    override val logType = LogUtils.LogType.ANDROID
    override val showClearCacheButton = true
}

private object Prod : EnvInterface() {
    override val serverUrl = "http://crm.cmit22.ru"
    override val logType = if (BuildConfig.DEBUG) {
        LogUtils.LogType.ANDROID
    } else {
        LogUtils.LogType.ANDROID_ERRORS
    }
    override val showClearCacheButton = BuildConfig.DEBUG
}