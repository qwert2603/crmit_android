package com.qwert2603.crmit_android.util

import com.qwert2603.andrlib.util.LogUtils
import io.reactivex.Completable
import io.reactivex.Single
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object ProxyUtils {

    inline fun <reified T> createDoubleProxy(q1: T, q2: T): T = Proxy
            .newProxyInstance(
                    T::class.java.classLoader,
                    arrayOf(T::class.java)
            ) { _: Any, method: Method, args: Array<out Any> ->
                LogUtils.d("createDoubleProxy $method ${method.returnType} $args")
                when (method.returnType.canonicalName) {
                    Completable::class.java.canonicalName -> {
                        LogUtils.d("createDoubleProxy is Completable")
                        method.invoke(q1, *args)
                                .let { it as Completable }
                                .onErrorResumeNext { method.invoke(q2, *args) as Completable }
                    }
                    Single::class.java.canonicalName -> {
                        LogUtils.d("createDoubleProxy is Single<*>")
                        method.invoke(q1, *args)
                                .let { it as Single<Any> }
                                .onErrorResumeNext(method.invoke(q2, *args) as Single<Any>)
                    }
                    else -> null
                }
            } as T

}