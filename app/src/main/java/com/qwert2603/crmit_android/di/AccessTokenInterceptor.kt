package com.qwert2603.crmit_android.di

import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.rest.Rest
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestUrl = chain.request().url().toString()
        if (requestUrl in Rest.ENDPOINT_WO_ACCESS_TOKEN.map { E.env.restBaseUrl + it }) {
            return chain.proceed(chain.request())
        }
        val accessToken = DiHolder.userSettingsRepo.getAccessTokenSafe()
        if (accessToken == null) {
            DiHolder.userSettingsRepo.on401()
            throw Exception("DiHolder.userSettingsRepo.loginResult == null")
        }
        val request = chain.request()
                .newBuilder()
                .url(
                        chain.request()
                                .url()
                                .newBuilder()
                                .addQueryParameter(Rest.QUERY_PARAM_ACCESS_TOKEN, accessToken)
                                .build()
                )
                .build()
        val response = chain.proceed(request)
        if (response.code() == Rest.RESPONSE_CODE_UNAUTHORIZED) {
            DiHolder.userSettingsRepo.clearAccessToken()
            DiHolder.userSettingsRepo.on401()
        }
        return response
    }
}