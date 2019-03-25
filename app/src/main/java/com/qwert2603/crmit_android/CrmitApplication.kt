package com.qwert2603.crmit_android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.annotation.FontRes
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.qwert2603.andrlib.base.mvi.load_refresh.list.listModelChangerInstance
import com.qwert2603.andrlib.base.mvi.load_refresh.lrModelChangerInstance
import com.qwert2603.andrlib.generated.LRModelChangerImpl
import com.qwert2603.andrlib.generated.ListModelChangerImpl
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.util.NoCacheException
import io.flutter.facade.Flutter
import io.flutter.plugins.GeneratedPluginRegistrant
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CrmitApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var APP_CONTEXT: Context

        @FontRes
        const val appFontRes = R.font.google_sans

        private fun setupLogs() {
            LogUtils.APP_TAG = "crmit"
            LogUtils.logType = E.env.logType
            LogUtils.errorsFilter = {
                when (it) {
                    is ConnectException -> false
                    is UnknownHostException -> false
                    is SocketTimeoutException -> false
                    is NoCacheException -> false
                    else -> true
                }
            }
            LogUtils.onErrorLogged = { tag, msg, t ->
                LogUtils.d("onErrorLogged") { "$tag $msg $t" }
                Crashlytics.log("$tag $msg $t")
                Crashlytics.logException(t)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

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

        setupLogs()

        DiHolder.userSettingsRepo.loginResult.field.t
                ?.let { "${it.accountType.name} ${it.detailsId}" }
                .also {
                    FirebaseAnalytics.getInstance(this)
                            .setUserProperty("loginResult", it ?: "n/a")
                }

        Flutter.startInitialization(this)
        GeneratedPluginRegistrant.flutterInterface = FlutterInterfaceImpl
    }
}