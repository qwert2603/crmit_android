package com.qwert2603.crmit_android.di

import android.arch.persistence.room.Room
import com.qwert2603.andrlib.schedulers.ModelSchedulersProvider
import com.qwert2603.andrlib.schedulers.UiSchedulerProvider
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.CrmitApplication
import com.qwert2603.crmit_android.db.LocalDB
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.rest.Rest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

object DiHolder {
    private val schedulersProvider by lazy { SchedulersProviderImpl() }

    val uiSchedulerProvider: UiSchedulerProvider by lazy { schedulersProvider }
    val modelSchedulersProvider: ModelSchedulersProvider by lazy { schedulersProvider }

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

    private val cicerone: Cicerone<Router>  by lazy { Cicerone.create() }

    val navigatorHolder: NavigatorHolder by lazy { cicerone.navigatorHolder }
    val router: Router  by lazy { cicerone.router }

    private val localDB by lazy {
        Room
                .databaseBuilder(CrmitApplication.APP_CONTEXT, LocalDB::class.java, "local.db")
                .build()
    }

    val masterDao by lazy { localDB.masterDao().wrap() }
    val teacherDao by lazy { localDB.teacherDao().wrap() }
    val studentBriefDao by lazy { localDB.studentBriefDao().wrap() }
    val studentFullDao by lazy { localDB.studentFullDao().wrap() }
    val sectionDao by lazy { localDB.sectionDao().wrap() }
}