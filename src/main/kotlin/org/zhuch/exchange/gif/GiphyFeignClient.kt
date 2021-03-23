package org.zhuch.exchange.gif

import org.zhuch.exchange.gif.dto.GiphyResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@ConditionalOnProperty(name = ["fetch.strategy"], havingValue = "feign")
class GiphyFeignClient(
    private val feignGiphyClient: FeignGiphyClient,
    @Value("\${giphy.api-key}")
    private val apiKey: String,
) : IGiphyClient {

    override fun searchGif(query: String, limit: Int, offset: Int): Mono<GiphyResponse> {
        return Mono.fromCallable {
            feignGiphyClient.searchGif(apiKey, query, limit, offset)
        }
            .log(GiphyFeignClient::class.qualifiedName)
    }
}
