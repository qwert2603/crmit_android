package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.entity.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("groups_list")
    fun getGroupsList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<GroupBrief>>

    @GET("student_details/{student_id}")
    fun getStudentDetails(@Path("student_id") studentId: Long): Single<StudentFull>

    @GET("group_details/{group_id}")
    fun getGroupDetails(@Path("group_id") groupId: Long): Single<GroupFull>

    @GET("section_details/{section_id}")
    fun getSectionDetails(@Path("section_id") sectionId: Long): Single<Section>
}