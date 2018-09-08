package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.entity.Section
import com.qwert2603.crmit_android.entity.Teacher
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Rest {

    @GET("teachers_list")
    fun getTeachersList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<Teacher>>

    @GET("sections_list")
    fun getSectionsList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<Section>>
}