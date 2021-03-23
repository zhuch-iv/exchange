package org.zhuch.exchange

import org.zhuch.exchange.currency.IExchangeRatesClient
import org.zhuch.exchange.domain.ExchangeService.Companion.RUB
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerSettings
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.kotlin.test.test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest
@ActiveProfiles("test")
@MockServerSettings(ports = [9090])
class ExchangeRatesClientTest @Autowired constructor(
    private val exchangeRates: IExchangeRatesClient
) {

    private lateinit var client: ClientAndServer

    @BeforeEach
    fun initServer(client: ClientAndServer) {
        this.client = client
    }

    @AfterEach
    fun cleanUpData() {
        client.reset()
    }

    @Test
    fun `success test latest`() {
        val currency = "GBP"
        successfulResponseLatest(client, currency)

        exchangeRates.latest(listOf(RUB, currency))
            .test()
            .expectNextMatches {
                it.rates[RUB] == 74.54475 && it.rates[currency] == 0.7147
            }
            .verifyComplete()
    }

    @Test
    fun `success test historical`() {
        val currency = "GBP"
        val date = LocalDate.now().minusDays(1)
        successfulResponseHistorical(client, currency, date)

        exchangeRates.historical(listOf(RUB, currency), date)
            .test()
            .expectNextMatches {
                it.rates[RUB] == 74.035 && it.rates[currency] == 0.713598
            }
            .verifyComplete()
    }

    companion object {
        fun successfulResponseLatest(client: ClientAndServer, currency: String) {
            client.`when`(getExchangeRatesRequest("/latest.json", currency), Times.exactly(1))
                .respond(
                    HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(JsonBody(getResourceAsString("exchange_response.json")))
                )
        }

        fun successfulResponseHistorical(client: ClientAndServer, currency: String, date: LocalDate) {
            val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            client.`when`(getExchangeRatesRequest("/historical/$dateStr.json", currency), Times.exactly(1))
                .respond(
                    HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(JsonBody(getResourceAsString("exchange_response_old.json")))
                )
        }

        private fun getExchangeRatesRequest(path: String, currency: String): HttpRequest {
            return HttpRequest.request()
                .withMethod("GET")
                .withPath(path)
                .withQueryStringParameter("symbols","RUB,$currency")
                .withQueryStringParameter("app_id","test")
        }

        private fun getResourceAsString(url: String): String {
            return this::class.java.classLoader.getResource(url)!!.readText()
        }
    }
}
