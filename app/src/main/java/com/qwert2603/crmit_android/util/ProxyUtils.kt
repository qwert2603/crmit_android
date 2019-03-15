package com.qwert2603.crmit_android.util

import android.os.SystemClock
import com.qwert2603.andrlib.util.Const
import com.qwert2603.andrlib.util.LogUtils
import io.reactivex.Completable
import io.reactivex.Single
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object ProxyUtils {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> createRxProxy(q1: T, q2: T): T {
        var nextTryQ1 = 0L

        return Proxy
                .newProxyInstance(
                        T::class.java.classLoader,
                        arrayOf(T::class.java)
                ) { _: Any, method: Method, argsNullable: Array<out Any>? ->
                    val args = argsNullable ?: arrayOf()
                    LogUtils.d("createRxProxy ${SystemClock.elapsedRealtime()} $nextTryQ1 $method ${method.returnType} $args")

                    if (SystemClock.elapsedRealtime() < nextTryQ1) {
                        LogUtils.d("createRxProxy using q2")
                        return@newProxyInstance method.invoke(q2, *args)
                    }

                    return@newProxyInstance when (method.returnType.canonicalName) {
                        Completable::class.java.canonicalName -> {
                            LogUtils.d("createRxProxy is Completable")
                            method.invoke(q1, *args)
                                    .let { it as Completable }
                                    .doOnError { nextTryQ1 = SystemClock.elapsedRealtime() + Const.MILLIS_PER_MINUTE }
                                    .onErrorResumeNext { method.invoke(q2, *args) as Completable }
                        }
                        Single::class.java.canonicalName -> {
                            LogUtils.d("createRxProxy is Single<*>")
                            method.invoke(q1, *args)
                                    .let { it as Single<Any> }
                                    .doOnError { nextTryQ1 = SystemClock.elapsedRealtime() + Const.MILLIS_PER_MINUTE }
                                    .onErrorResumeNext(method.invoke(q2, *args) as Single<Any>)
                        }
                        else -> throw Exception("createRxProxy wrong returnType ${method.returnType.canonicalName} $method")
                    }
                } as T
    }
}