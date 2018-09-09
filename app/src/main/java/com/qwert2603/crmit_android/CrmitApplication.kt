package com.qwert2603.crmit_android

import android.app.Application
import android.os.Looper
import com.qwert2603.andrlib.base.mvi.load_refresh.list.listModelChangerInstance
import com.qwert2603.andrlib.base.mvi.load_refresh.lrModelChangerInstance
import com.qwert2603.andrlib.generated.LRModelChangerImpl
import com.qwert2603.andrlib.generated.ListModelChangerImpl
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.env.E
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins

class CrmitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler {
            LogUtils.e("RxJavaPlugins.setErrorHandler", it)
            var cause = it.cause
            while (cause != null) {
                LogUtils.e("RxJavaPlugins.setErrorHandler", cause)
                cause = cause.cause
            }
        }

        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }

        lrModelChangerInstance = LRModelChangerImpl()
        listModelChangerInstance = ListModelChangerImpl()

        LogUtils.logType = E.env.logType
    }
}