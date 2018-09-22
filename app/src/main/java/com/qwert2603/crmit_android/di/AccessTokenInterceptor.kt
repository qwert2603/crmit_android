package com.qwert2603.crmit_android.di

import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.rest.Rest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

class AccessTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().url().toString() == E.env.restBaseUrl + Rest.LOGIN_ENDPOINT) {
            return chain.proceed(chain.request())
        }
        val accessToken = DiHolder.userSettingsRepo.loginResult?.token
        if (accessToken == null) {
            return Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(DiHolder.RESPONSE_CODE_UNAUTHORIZED)
                    .message("UNAUTHORIZED")
                    .body(ResponseBody.create(null, "userSettingsRepo.accessToken == null"))
                    .build()
        }
        val request = chain.request()
                .newBuilder()
                .addHeader(DiHolder.HEADER_ACCESS_TOKEN, accessToken)
                .build()
        return chain.proceed(request)
    }
}