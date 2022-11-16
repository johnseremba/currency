package com.johnseremba.currency.base

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.johnseremba.currency.data.api.CurrencyService
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class MockWebServerBaseTest {
    @Inject
    lateinit var okHttpClient: OkHttpClient

    lateinit var mockWebServer: MockWebServer
    lateinit var currencyApi: CurrencyService

    @Before
    open fun setUp() {
        mockWebServer = MockWebServer().apply { start(8080) }
        okHttpClient = OkHttpClient.Builder().build()
        currencyApi = createCurrencyService()
    }

    @After
    open fun tearDown() {
        mockWebServer.shutdown()
    }

    fun getServerUrl() = mockWebServer.url("").toString()

    private fun createCurrencyService(): CurrencyService {
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()

        return Retrofit.Builder()
            .baseUrl(getServerUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CurrencyService::class.java)
    }
}
