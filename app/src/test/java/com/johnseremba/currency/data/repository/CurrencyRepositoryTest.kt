package com.johnseremba.currency.data.repository

import com.johnseremba.currency.base.MockWebServerBaseTest
import com.johnseremba.currency.di.CoroutineDispatcherProvider
import com.johnseremba.currency.di.TestCoroutineDispatcherProvider
import com.johnseremba.currency.util.TestResources
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
import java.net.HttpURLConnection
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyRepositoryTest : MockWebServerBaseTest() {
    private val dispatcherProvider: CoroutineDispatcherProvider = TestCoroutineDispatcherProvider()
    private lateinit var currencyRepository: Repository

    @Before
    override fun setUp() {
        super.setUp()
        currencyRepository = CurrencyRepository(currencyApi, dispatcherProvider)
        Dispatchers.setMain(dispatcherProvider.io)
    }

    @After
    override fun tearDown() {
        super.tearDown()
        Dispatchers.resetMain()
    }

    @Test
    fun testGetSupportedCurrencies() = runTest {
        // Given
        setApiResponse(fileName = "supported_currency_response.json")

        // When
        val result = currencyRepository.getSupportedCurrencies()

        // Then
        val expected: Result<List<String>> = Result.success(listOf("AED", "RUB", "RWF", "TZS", "UGX", "USD"))
        assertEquals(expected, result)
    }

    @Test
    fun testCurrencyConversion() = runTest {
        // Given
        setApiResponse(fileName = "convert_response.json")

        // When
        val result = currencyRepository.convertCurrency("EUR", "UGX", BigDecimal(85000.00).setScale(2, HALF_EVEN))

        // Then
        val expected: Result<BigDecimal> = Result.success(BigDecimal(329763742.145).setScale(3, HALF_EVEN))
        assertEquals(expected, result)
    }

    @Test
    fun testHistoricalData() = runTest {
        // Given
        setApiResponse("timeseries_response.json")

        // When
        val result = currencyRepository.getHistoricalData(
            startDate = LocalDate.parse("2022-11-13"),
            endDate = LocalDate.parse("2022-11-15"),
            baseCurrency = "EUR",
            targetCurrency = "UGX"
        )

        // Then
        val expected: Result<List<Pair<LocalDate, BigDecimal>>> = Result.success(
            listOf(
                Pair(LocalDate.parse("2022-11-13"), BigDecimal(3857.744946).setScale(6, HALF_EVEN)),
                Pair(LocalDate.parse("2022-11-14"), BigDecimal(3876.729851).setScale(6, HALF_EVEN)),
                Pair(LocalDate.parse("2022-11-15"), BigDecimal(3869.225617).setScale(6, HALF_EVEN)),
            )
        )

        assertEquals(expected.getOrNull(), result.getOrNull())
    }

    @Test
    fun testPopularCurrencyData() = runTest {
        // Given
        setApiResponse("popular_currencies.json")

        // When
        val result: Result<List<Pair<String, BigDecimal>>> = currencyRepository.getRatesForPopularCurrencies("EUR")

        // Then
        val expected = Result.success(
            listOf(
                Pair("AUD", BigDecimal(1.528537).setScale(6, HALF_EVEN)),
                Pair("CAD", BigDecimal(1.372642).setScale(6, HALF_EVEN)),
                Pair("CHF", BigDecimal(0.976349).setScale(6, HALF_EVEN)),
                Pair("CNY", BigDecimal(7.290138).setScale(6, HALF_EVEN)),
                Pair("EUR", BigDecimal(1).setScale(0, HALF_EVEN)),
                Pair("GBP", BigDecimal(0.871849).setScale(6, HALF_EVEN)),
                Pair("HKD", BigDecimal(8.079658).setScale(6, HALF_EVEN)),
                Pair("JPY", BigDecimal(143.927912).setScale(6, HALF_EVEN)),
                Pair("NZD", BigDecimal(1.677855).setScale(6, HALF_EVEN)),
                Pair("USD", BigDecimal(1.032978).setScale(6, HALF_EVEN)),
            )
        )

        assertEquals(expected.getOrNull(), result.getOrNull())
    }

    private fun setApiResponse(fileName: String) {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(TestResources.getJsonAsString(fileName))
                .setHeader("Content-Type", "application/json")
        )
    }
}
