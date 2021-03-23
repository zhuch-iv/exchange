package org.zhuch.exchange.controller

import org.zhuch.exchange.domain.Exchange
import org.zhuch.exchange.domain.ExchangeService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class ExchangeController(
    private val service: ExchangeService
) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE], path = ["/json/{currency}"])
    fun json(@PathVariable("currency") currency: String): Mono<Exchange> {
        return service.getExchange(currency)
    }


    @GetMapping(produces = [MediaType.IMAGE_GIF_VALUE], path = ["/gif/{currency}"])
    fun gif(@PathVariable("currency") currency: String): Mono<ByteArray> {
        return service.getGif(currency)
    }

}
