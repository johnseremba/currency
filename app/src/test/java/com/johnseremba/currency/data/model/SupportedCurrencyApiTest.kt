package com.johnseremba.currency.data.model

import com.johnseremba.currency.base.MockWebServerBaseTest
import com.johnseremba.currency.data.api.model.CurrencySymbolsApiModel
import com.johnseremba.currency.util.TestResources
import java.net.HttpURLConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupportedCurrencyApiTest : MockWebServerBaseTest() {

    @Test
    fun testCurrencyListApiRequest() = runTest {
        // Given
        setApiListResponse()

        // When
        currencyApi.getSupportedCurrencies()
        val request = mockWebServer.takeRequest()

        // Then
        assertEquals("${getServerUrl()}fixer/symbols", request.requestUrl.toString())
    }

    @Test
    fun testCurrencyListApiResponse() = runTest {
        // Given
        setApiListResponse()

        // When
        val response: CurrencySymbolsApiModel = currencyApi.getSupportedCurrencies()

        // Then
        assertEquals(6, response.symbols.values.size)

        val currencyList: List<String> = listOf("AED", "RUB", "RWF", "TZS", "UGX", "USD")
        assertEquals(currencyList, response.symbols.keys.toList())
    }

    private fun setApiListResponse() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(TestResources.getJsonAsString("supported_currency_response.json"))
                .setHeader("Content-Type", "application/json")
        )
    }
}
