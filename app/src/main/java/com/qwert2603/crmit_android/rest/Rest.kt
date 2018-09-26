package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.entity.*
import com.qwert2603.crmit_android.rest.params.LoginParams
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import com.qwert2603.crmit_android.rest.params.SavePaymentParams
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface Rest {

    companion object {
        const val HEADER_ACCESS_TOKEN = "access_token"

        const val RESPONSE_CODE_UNAUTHORIZED = 401
        const val RESPONSE_CODE_BAD_REQUEST = 400

        const val DATE_FORMAT = "yyyy-MM-dd"

        const val LOGIN_ENDPOINT = "login"
    }

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

    @GET("master_details/{master_id}")
    fun getMasterDetails(@Path("master_id") masterId: Long): Single<Master>

    @GET("teacher_details/{teacher_id}")
    fun getTeacherDetails(@Path("teacher_id") teacherId: Long): Single<Teacher>

    @GET("students_in_group/{group_id}")
    fun getStudentsInGroup(@Path("group_id") groupId: Long): Single<List<StudentInGroup>>

    @GET("lessons_in_group/{group_id}")
    fun getLessonsInGroup(@Path("group_id") groupId: Long): Single<List<Lesson>>

    @GET("attendings_of_lesson/{lesson_id}")
    fun getAttendingsOfLesson(@Path("lesson_id") lessonId: Long): Single<List<Attending>>

    @GET("payments/{group_id}/{month_number}")
    fun getPayments(
            @Path("group_id") groupId: Long,
            @Path("month_number") monthNumber: Int
    ): Single<List<Payment>>

    @POST("save_attending_state")
    fun saveAttendingState(@Body saveAttendingStateParams: SaveAttendingStateParams): Completable

    @POST("save_payment")
    fun savePayment(@Body savePaymentParams: SavePaymentParams): Completable

    @POST(LOGIN_ENDPOINT)
    fun login(@Body loginParams: LoginParams): Single<LoginResult>

    @POST("logout")
    fun logout(): Completable
}