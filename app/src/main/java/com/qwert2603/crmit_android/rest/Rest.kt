package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.entity.*
import com.qwert2603.crmit_android.rest.params.CabinetInfoParams
import com.qwert2603.crmit_android.rest.params.LoginParams
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import com.qwert2603.crmit_android.rest.params.SavePaymentParams
import com.qwert2603.crmit_android.rest.results.CabinetInfoResult
import com.qwert2603.crmit_android.rest.results.LessonDetailsResult
import com.qwert2603.crmit_android.rest.results.LoginResultServer
import com.qwert2603.crmit_android.rest.results.PaymentsInGroupResult
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface Rest {

    companion object {
        const val QUERY_PARAM_ACCESS_TOKEN = "access_token"

        const val RESPONSE_CODE_UNAUTHORIZED = 401
        const val RESPONSE_CODE_BAD_REQUEST = 400

        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"

        const val ENDPOINT_LOGIN = "login"
        const val ENDPOINT_APP_INFO = "app_info"

        val ENDPOINT_WO_ACCESS_TOKEN = listOf(
                ENDPOINT_LOGIN,
                ENDPOINT_APP_INFO
        )
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

    @GET("developers_list")
    fun getDevelopersList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<Developer>>

    @GET("bots_list")
    fun getBotsList(
            @Query("offset") offset: Int,
            @Query("count") count: Int,
            @Query("search") search: String
    ): Single<List<Bot>>

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

    @GET("developer_details/{developer_id}")
    fun getDeveloperDetails(@Path("developer_id") developerId: Long): Single<Developer>

    @GET("bot_details/{bot_id}")
    fun getBotDetails(@Path("bot_id") botId: Long): Single<Bot>

    @GET("master_details/{master_id}")
    fun getMasterDetails(@Path("master_id") masterId: Long): Single<Master>

    @GET("teacher_details/{teacher_id}")
    fun getTeacherDetails(@Path("teacher_id") teacherId: Long): Single<Teacher>

    @GET("students_in_group/{group_id}")
    fun getStudentsInGroup(@Path("group_id") groupId: Long): Single<List<StudentInGroup>>

    @GET("lessons_in_group/{group_id}")
    fun getLessonsInGroup(@Path("group_id") groupId: Long): Single<List<Lesson>>

    @POST("cabinet_info")
    fun getCabinetInfo(@Body cabinetInfoParams: CabinetInfoParams): Single<CabinetInfoResult>

    @GET("lesson_details/{lesson_id}")
    fun getLessonDetails(@Path("lesson_id") lessonId: Long): Single<LessonDetailsResult>

    @GET("payments/{group_id}")
    fun getPaymentsInGroup(@Path("group_id") groupId: Long): Single<PaymentsInGroupResult>

    @POST("save_attending_state")
    fun saveAttendingState(@Body saveAttendingStateParams: SaveAttendingStateParams): Completable

    @POST("save_payment")
    fun savePayment(@Body savePaymentParams: SavePaymentParams): Completable

    @POST(ENDPOINT_LOGIN)
    fun login(@Body loginParams: LoginParams): Single<LoginResultServer>

    @POST("logout")
    fun logout(): Completable

    @GET(ENDPOINT_APP_INFO)
    fun appInfo(): Single<AppInfo>

    @GET("last_seens")
    fun getLastSeens(): Single<List<LastSeenItem>>

    @GET("access_tokens")
    fun getAccessTokens(): Single<List<AccessTokensItem>>
}