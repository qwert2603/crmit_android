package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.entity.Master
import com.qwert2603.crmit_android.entity.Section
import com.qwert2603.crmit_android.entity.StudentBrief
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

    @GET("masters_list")
    fun getMastersList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<Master>>

    @GET("students_list")
    fun getStudentsList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<StudentBrief>>

    @GET("sections_list")
    fun getSectionsList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<Section>>
}