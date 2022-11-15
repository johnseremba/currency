package com.johnseremba.currency.data.model

import com.johnseremba.currency.base.MockWebServerBaseTest
import com.johnseremba.currency.data.api.model.LatestRatesApiModel
import com.johnseremba.currency.util.TestResources
import java.net.HttpURLConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LatestRatesApiTest : MockWebServerBaseTest() {
    @Test
    fun testLatestRatesRequest() = runTest {
        // Given
        setLatestRatesResponse()

        // When
        currencyApi.getLatestRates("EUR", listOf("GBP", "JPY", "EUR", "USD", "AUD", "CAD", "CHF", "CNH", "HKD", "NZD").joinToString(","))
        val request = mockWebServer.takeRequest()

        // Then
        assertEquals("${getServerUrl()}fixer/latest?base=EUR&symbols=GBP%2CJPY%2CEUR%2CUSD%2CAUD%2CCAD%2CCHF%2CCNH%2CHKD%2CNZD", request.requestUrl.toString())
    }

    @Test
    fun testPopularCurrencyRates() = runTest {
        // Given
        setLatestRatesResponse()

        // When
        val popularCurrencies = listOf("GBP", "JPY", "EUR", "USD", "AUD", "CAD", "CHF", "CNY", "HKD", "NZD")
        val response: LatestRatesApiModel = currencyApi.getLatestRates("EUR", popularCurrencies.joinToString(","))

        // Then
        val currencies = response.rates.keys.toList()
        assertEquals(popularCurrencies.sorted(), currencies.sorted())

        // And
        assertEquals(1.528537, response.rates["AUD"]!!.toDouble(), 0.6)
        assertEquals(1.372642, response.rates["CAD"]!!.toDouble(), 0.6)
        assertEquals(0.976349, response.rates["CHF"]!!.toDouble(), 0.6)
        assertEquals(7.290138, response.rates["CNY"]!!.toDouble(), 0.6)
        assertEquals(1, response.rates["EUR"]!!.toInt())
        assertEquals(0.871849, response.rates["GBP"]!!.toDouble(), 0.6)
        assertEquals(8.079658, response.rates["HKD"]!!.toDouble(), 0.6)
        assertEquals(143.927912, response.rates["JPY"]!!.toDouble(), 0.6)
        assertEquals(1.677855, response.rates["NZD"]!!.toDouble(), 0.6)
        assertEquals(1.032978, response.rates["USD"]!!.toDouble(), 0.6)
    }

    private fun setLatestRatesResponse() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(TestResources.getJsonAsString("popular_currencies.json"))
                .setHeader("Content-Type", "application/json")
        )
    }
}
