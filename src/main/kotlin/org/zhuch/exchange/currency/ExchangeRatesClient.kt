package org.zhuch.exchange.currency

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.LocalDate
import java.util.logging.Level

import java.time.format.DateTimeFormatter

@Component
@ConditionalOnProperty(name = ["fetch.strategy"], havingValue = "webflux")
class ExchangeRatesClient(
    httpClient: HttpClient,
    @Value("\${open-exchange-rates.url}")
    url: String,
    @Value("\${open-exchange-rates.api-key}")
    private val apiKey: String
) : IExchangeRatesClient {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .baseUrl(url)
        .build()

    override fun latest(currencies: List<String>): Mono<Rates> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/latest.json")
                    .queryParam("app_id", apiKey)
                    .queryParam("symbols", currencies.joinToString(","))
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Rates::class.java)
            .log(ExchangeRatesClient::class.qualifiedName, Level.INFO)
    }

    override fun historical(currencies: List<String>, date: LocalDate): Mono<Rates> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/historical/{date}.json")
                    .queryParam("app_id", apiKey)
                    .queryParam("symbols", currencies.joinToString(","))
                    .build(date.format(formatter))
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Rates::class.java)
            .log(ExchangeRatesClient::class.qualifiedName, Level.INFO)
    }
}
