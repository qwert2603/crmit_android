package com.qwert2603.crmit_android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Looper
import com.qwert2603.andrlib.base.mvi.load_refresh.list.listModelChangerInstance
import com.qwert2603.andrlib.base.mvi.load_refresh.lrModelChangerInstance
import com.qwert2603.andrlib.generated.LRModelChangerImpl
import com.qwert2603.andrlib.generated.ListModelChangerImpl
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.env.E
import com.squareup.leakcanary.LeakCanary
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins

class CrmitApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var APP_CONTEXT: Context
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) return
//        LeakCanary.install(this)

        LogUtils.d("CrmitApplication onCreate")

        APP_CONTEXT = this

        StethoInstaller.install(this)

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