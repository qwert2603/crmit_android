package com.qwert2603.crmit_android

import android.app.Application
import com.qwert2603.andrlib.base.mvi.load_refresh.list.listModelChangerInstance
import com.qwert2603.andrlib.base.mvi.load_refresh.lrModelChangerInstance
import com.qwert2603.andrlib.generated.LRModelChangerImpl
import com.qwert2603.andrlib.generated.ListModelChangerImpl

class CrmitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        lrModelChangerInstance = LRModelChangerImpl()
        listModelChangerInstance = ListModelChangerImpl()
    }
}