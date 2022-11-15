package com.johnseremba.currency.data.model

import com.johnseremba.currency.base.MockWebServerBaseTest
import com.johnseremba.currency.data.api.model.ConvertApiModel
import com.johnseremba.currency.util.TestResources
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConvertCurrencyApiTest : MockWebServerBaseTest() {

    @Test
    fun testCurrencyConversionRequest() = runTest {
        // Given
        setCurrencyConversionResponse()

        // When
        currencyApi.convert("EUR", "UGX", 10000.05)
        val request = mockWebServer.takeRequest()

        // Then
        assertEquals("${getServerUrl()}fixer/convert?from=EUR&to=UGX&amount=10000.05", request.requestUrl.toString())
    }

    @Test
    fun testTargetCurrencyConversion() = runTest {
        // Given
        setCurrencyConversionResponse()

        // When
        val response: ConvertApiModel = currencyApi.convert("EUR", "UGX", 85000.00)

        // Then
        assertEquals("EUR", response.query.baseCurrency)
        assertEquals("UGX", response.query.targetCurrency)
    }

    @Test
    fun testCurrencyConversionAmount() = runTest {
        // Given
        setCurrencyConversionResponse()

        // When
        val response: ConvertApiModel = currencyApi.convert("EUR", "UGX", 85000.00)

        // Then
        assertEquals(3879.573437, response.info.rate.toDouble(), 0.6)
        assertEquals(85000.00, response.query.amount.toDouble(), 0.2)
        assertEquals(329763742.145, response.convertedAmount.toDouble(), 0.3)
    }

    @Test
    fun testConversionDateFormat() = runTest {
        // Given
        setCurrencyConversionResponse()

        // When
        val response: ConvertApiModel = currencyApi.convert("EUR", "UGX", 85000.00)

        // Then
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.format(response.date)
        assertEquals("2022-11-15", date)
    }

    private fun setCurrencyConversionResponse() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(TestResources.getJsonAsString("convert_response.json"))
                .setHeader("Content-Type", "application/json")
        )
    }
}
