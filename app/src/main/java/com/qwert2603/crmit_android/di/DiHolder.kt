package com.qwert2603.crmit_android.di

import android.arch.persistence.room.Room
import android.content.res.Resources
import com.qwert2603.andrlib.schedulers.ModelSchedulersProvider
import com.qwert2603.andrlib.schedulers.UiSchedulerProvider
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.CrmitApplication
import com.qwert2603.crmit_android.db.LocalDB
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.rest.Rest
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

object DiHolder {
    private const val HEADER_ACCESS_TOKEN = "access_token"
    private const val RESPONSE_CODE_UNAUTHORIZED = 401
    const val RESPONSE_CODE_BAD_REQUEST = 400

    private val schedulersProvider by lazy { SchedulersProviderImpl() }

    val uiSchedulerProvider: UiSchedulerProvider by lazy { schedulersProvider }
    val modelSchedulersProvider: ModelSchedulersProvider by lazy { schedulersProvider }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor { message -> LogUtils.d("ok_http", message) }.setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor {
                    if (it.request().url().toString() == E.env.restBaseUrl + Rest.LOGIN_ENDPOINT) {
                        return@addInterceptor it.proceed(it.request())
                    }
                    val accessToken = userSettingsRepo.loginResult?.token
                    if (accessToken == null) {
                        return@addInterceptor Response.Builder()
                                .request(it.request())
                                .protocol(Protocol.HTTP_1_1)
                                .code(RESPONSE_CODE_UNAUTHORIZED)
                                .message("UNAUTHORIZED")
                                .body(ResponseBody.create(null, "userSettingsRepo.accessToken == null"))
                                .build()
                    }
                    val request = it.request()
                            .newBuilder()
                            .addHeader(HEADER_ACCESS_TOKEN, accessToken)
                            .build()
                    it.proceed(request)
                }
                .build()
    }

    val rest: Rest by lazy {
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
                .fallbackToDestructiveMigration()
                .build()
    }

    val masterDaoInterface by lazy { localDB.masterDao().wrap() }
    val teacherDaoInterface by lazy { localDB.teacherDao().wrap() }
    val studentBriefDaoInterface by lazy { localDB.studentBriefDao().wrap() }
    val studentFullDaoInterface by lazy { localDB.studentFullDao().wrap() }
    val sectionDaoInterface by lazy { localDB.sectionDao().wrap() }
    val groupBriefDaoInterface by lazy { localDB.groupBriefDao().wrap() }
    val groupFullDaoInterface by lazy { localDB.groupFullDao().wrap() }

    val studentInGroupDao by lazy { localDB.studentInGroupDao() }
    val lessonDao by lazy { localDB.lessonDao() }
    val attendingDao by lazy { localDB.attendingDao() }

    val userSettingsRepo by lazy { UserSettingsRepo(CrmitApplication.APP_CONTEXT) }

    val resources: Resources by lazy { CrmitApplication.APP_CONTEXT.resources }
}