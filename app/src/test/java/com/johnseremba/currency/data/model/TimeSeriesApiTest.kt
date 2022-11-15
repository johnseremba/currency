package com.johnseremba.currency.data.model

import com.johnseremba.currency.base.MockWebServerBaseTest
import com.johnseremba.currency.data.api.model.TimeSeriesApiModel
import com.johnseremba.currency.util.TestResources
import java.math.BigDecimal
import java.net.HttpURLConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimeSeriesApiTest : MockWebServerBaseTest() {
    @Test
    fun testTimeSeriesRequest() = runTest {
        // Given
        setTimeSeriesResponse()

        // When
        currencyApi.getTimeSeries("2022-11-13", "2022-11-15", "EUR", "UGX")
        val request = mockWebServer.takeRequest()

        // Then
        assertEquals("${getServerUrl()}fixer/timeseries?start_date=2022-11-13&end_date=2022-11-15&base=EUR&symbols=UGX", request.requestUrl.toString())
    }

    @Test
    fun testTimeSeriesDataPerDay() = runTest {
        // Given
        setTimeSeriesResponse()

        // When
        val response: TimeSeriesApiModel = currencyApi.getTimeSeries("2022-11-13", "2022-11-15", "EUR", "UGX")

        // Then
        val dates = response.rates.keys.toList()
        assertEquals("2022-11-13", dates[0])
        assertEquals("2022-11-14", dates[1])
        assertEquals("2022-11-15", dates[2])

        // And
        val rate1: Map<String, BigDecimal> = response.rates["2022-11-13"]!!
        assertEquals(3857.744946, rate1["UGX"]!!.toDouble(), 0.6)

        val rate2: Map<String, BigDecimal> = response.rates["2022-11-14"]!!
        assertEquals(3876.729851, rate2["UGX"]!!.toDouble(), 0.6)

        val rate3: Map<String, BigDecimal> = response.rates["2022-11-15"]!!
        assertEquals(3869.225617, rate3["UGX"]!!.toDouble(), 0.6)
    }

    private fun setTimeSeriesResponse() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(TestResources.getJsonAsString("timeseries_response.json"))
                .setHeader("Content-Type", "application/json")
        )
    }
}
