package org.zhuch.exchange.currency

import reactor.core.publisher.Mono
import java.time.LocalDate

interface IExchangeRatesClient {

    fun latest(currencies: List<String>): Mono<Rates>

    fun historical(currencies: List<String>, date: LocalDate): Mono<Rates>
}
