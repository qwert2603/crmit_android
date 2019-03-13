package com.qwert2603.crmit_android.di

import androidx.room.Room
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
                .addInterceptor(AccessTokenInterceptor())
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
                .fallbackToDestructiveMigrationFrom(1)
                .build()
    }

    val masterDaoInterface by lazy { localDB.masterDao().wrap() }
    val teacherDaoInterface by lazy { localDB.teacherDao().wrap() }
    val studentBriefDaoInterface by lazy { localDB.studentBriefDao().wrap() }
    val studentFullDaoInterface by lazy { localDB.studentFullDao().wrap() }
    val sectionDaoInterface by lazy { localDB.sectionDao().wrap() }
    val groupFullDaoInterface by lazy { localDB.groupFullDao().wrap() }

    val studentInGroupDao by lazy { localDB.studentInGroupDao() }
    val lessonDao by lazy { localDB.lessonDao() }
    val attendingDao by lazy { localDB.attendingDao() }
    val paymentDao by lazy { localDB.paymentDao() }
    val groupBriefCustomOrderDao by lazy { localDB.groupBriefCustomOrderDao() }
    val lastLessonDao by lazy { localDB.lastLessonDao() }

    fun clearDB() {
        listOf(
                masterDaoInterface,
                teacherDaoInterface,
                studentBriefDaoInterface,
                studentFullDaoInterface,
                sectionDaoInterface,
                groupFullDaoInterface
        ).forEach { it.deleteAllItems() }

        studentInGroupDao.clearTable()
        lessonDao.clearTable()
        attendingDao.clearTable()
        paymentDao.clearTable()
        groupBriefCustomOrderDao.clearTable()
    }

    val userSettingsRepo by lazy { UserSettingsRepo(CrmitApplication.APP_CONTEXT) }

    val resources: Resources by lazy { CrmitApplication.APP_CONTEXT.resources }
}