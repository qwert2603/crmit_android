package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.entity.Section
import com.qwert2603.crmit_android.entity.Teacher
import io.reactivex.Single
import retrofit2.http.GET

interface Rest {

    @GET("teachers_list")
    fun getTeachersList(): Single<List<Teacher>>

    @GET("sections_list")
    fun getSectionsList(): Single<List<Section>>
}