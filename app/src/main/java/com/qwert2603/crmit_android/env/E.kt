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
    companion object {
        private const val API_PREFIX = "/api/v1.1.0/"
    }

    protected abstract val serverUrl1: String
    protected abstract val serverUrl2: String
    val restBaseUrl1 by lazy { "$serverUrl1$API_PREFIX" }
    val restBaseUrl2 by lazy { "$serverUrl2$API_PREFIX" }
    abstract val logType: LogUtils.LogType
    abstract val showClearCacheButton: Boolean
    open val useProxy = false
}

private object Home : EnvInterface() {
    override val serverUrl1 = "http://192.168.1.26:2603"
    override val serverUrl2 = "http://192.168.1.26:1918"
    override val logType = LogUtils.LogType.ANDROID
    override val showClearCacheButton = true
}

private object Prod : EnvInterface() {
    override val serverUrl1 = "http://crm.cmit22.ru:1918"
    override val serverUrl2 = "http://crm.cmit22.ru"
    override val logType = if (BuildConfig.DEBUG) {
        LogUtils.LogType.ANDROID
    } else {
        LogUtils.LogType.ANDROID_ERRORS
    }
    override val showClearCacheButton = BuildConfig.DEBUG
}