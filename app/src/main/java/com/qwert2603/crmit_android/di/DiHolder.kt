package com.qwert2603.crmit_android.di

import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.rest.Rest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object DiHolder {
    private val schedulersProvider by lazy { SchedulersProviderImpl() }

    val uiSchedulerProvider by lazy { schedulersProvider }
    val modelSchedulersProvider by lazy { schedulersProvider }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor { message -> LogUtils.d("ok_http", message) }.setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
    }

    val rest by lazy {
        Retrofit.Builder()
                .baseUrl(E.env.restBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(Rest::class.java)
    }
}