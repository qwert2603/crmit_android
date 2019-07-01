package com.qwert2603.crmit_android.rest

import com.qwert2603.crmit_android.di.DiHolder
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor(private val restBaseUrl: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestUrl = chain.request().url().toString()
        if (requestUrl in Rest.ENDPOINT_WO_ACCESS_TOKEN.map { restBaseUrl + it }) {
            return chain.proceed(chain.request())
        }
        val accessToken = DiHolder.userSettingsRepo.getAccessTokenSafe()
        if (accessToken == null) {
            DiHolder.on401()
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
            DiHolder.on401()
        }
        return response
    }
}