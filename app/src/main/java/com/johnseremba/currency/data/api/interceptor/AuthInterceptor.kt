package com.johnseremba.currency.data.api.interceptor

import com.johnseremba.currency.BuildConfig
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor @Inject constructor() : Interceptor {
    private companion object {
        private const val HEADER_API_KEY = "apikey"
    }

    override fun intercept(chain: Chain): Response {
        val request = chain.request()
        return chain.proceed(request.addApiKey())
    }

    private fun Request.addApiKey(): Request {
        val apiKey: String = BuildConfig.FIXER_API_KEY

        return newBuilder()
            .addHeader(HEADER_API_KEY, apiKey)
            .build()
    }
}
