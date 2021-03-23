package org.zhuch.exchange.currency

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
@ConditionalOnProperty(name = ["fetch.strategy"], havingValue = "feign")
class ExchangeRatesFeignClient(
    private val feignExchangeRatesClient: FeignExchangeRatesClient,
    @Value("\${open-exchange-rates.api-key}")
    private val apiKey: String,
): IExchangeRatesClient {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun latest(currencies: List<String>): Mono<Rates> {
        return Mono.fromCallable {
            feignExchangeRatesClient.latest(apiKey, currencies.joinToString(","))
        }
            .log(ExchangeRatesFeignClient::class.qualifiedName)
    }

    override fun historical(currencies: List<String>, date: LocalDate): Mono<Rates> {
        return Mono.fromCallable {
            feignExchangeRatesClient.historical(apiKey, currencies.joinToString(","), date.format(formatter))
        }
            .log(ExchangeRatesFeignClient::class.qualifiedName)
    }
}
