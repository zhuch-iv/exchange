package org.zhuch.exchange

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import org.zhuch.exchange.controller.ErrorMessage
import org.zhuch.exchange.currency.IExchangeRatesClient
import org.zhuch.exchange.currency.Rates
import org.zhuch.exchange.domain.Exchange
import org.zhuch.exchange.domain.ExchangeService.Companion.RUB
import org.zhuch.exchange.gif.IGiphyClient
import org.zhuch.exchange.gif.dto.Downsized
import org.zhuch.exchange.gif.dto.Gif
import org.zhuch.exchange.gif.dto.GiphyResponse
import org.zhuch.exchange.gif.dto.Images
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class ExchangeControllerTest {
    @MockBean
    private lateinit var exchangeRatesClient: IExchangeRatesClient
    @MockBean
    private lateinit var giphyClient: IGiphyClient

    @Autowired
    private lateinit var applicationContext: ApplicationContext
    lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun init() {
        webTestClient = WebTestClient
            .bindToApplicationContext(applicationContext)
            .build()
    }

    @Test
    fun `success test`() {
        exchangeRatesSuccessResponse()
        giphyClientSuccessResponse()

        webTestClient.get()
            .uri("/api/json/$TEST_CURRENCY")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Exchange::class.java)
            .responseBody
            .test()
            .expectNextMatches {
                it.type == Exchange.Type.RICH &&
                    it.latest == 74.5 &&
                    it.yesterday == 73.0 &&
                    it.gifUrl == "TEST_URL"
            }
            .verifyComplete()
    }

    @Test
    fun `bad currency test`() {
        exchangeRatesBadCurrency()

        webTestClient.get()
            .uri("/api/json/$BAD_CURRENCY")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .returnResult(ErrorMessage::class.java)
            .responseBody
            .test()
            .expectNextMatches {
                it.message == "Currency with ticker '$BAD_CURRENCY' not available" &&
                    it.statusCode == 404
            }
            .verifyComplete()
    }

    fun exchangeRatesSuccessResponse() {
        Mockito.`when`(exchangeRatesClient.latest(eq(listOf(TEST_CURRENCY, RUB))))
            .thenReturn(Mono.just(getTestRates(TEST_CURRENCY, 1.0, 74.5)))

        val date = LocalDate.now().minusDays(1)
        Mockito.`when`(exchangeRatesClient.historical(eq(listOf(TEST_CURRENCY, RUB)), eq(date)))
            .thenReturn(Mono.just(getTestRates(TEST_CURRENCY, 1.0, 73.0)))
    }

    fun giphyClientSuccessResponse() {
        Mockito.`when`(giphyClient.searchGif(eq("rich"), eq(1), any()))
            .thenReturn(Mono.just(getTestGiphyResponse()))
    }

    fun exchangeRatesBadCurrency() {
        Mockito.`when`(exchangeRatesClient.latest(eq(listOf(BAD_CURRENCY, RUB))))
            .thenReturn(Mono.just(getRubRate()))

        val date = LocalDate.now().minusDays(1)
        Mockito.`when`(exchangeRatesClient.historical(eq(listOf(BAD_CURRENCY, RUB)), eq(date)))
            .thenReturn(Mono.just(getRubRate()))
    }

    companion object {
        fun getTestGiphyResponse(): GiphyResponse {
            return GiphyResponse(
                data = listOf(Gif(images = Images(downsized = Downsized(url = "TEST_URL"))))
            )
        }

        fun getTestRates(currency: String, rate: Double, rubRate: Double): Rates {
            return Rates(
                rates = mutableMapOf(Pair("RUB", rubRate), Pair(currency, rate))
            )
        }

        fun getRubRate(): Rates {
            return Rates(
                rates = mutableMapOf(Pair("RUB", 73.0))
            )
        }

        const val TEST_CURRENCY = "USD"
        const val BAD_CURRENCY = "ZZZ"
    }
}
