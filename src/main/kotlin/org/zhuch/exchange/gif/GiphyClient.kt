package org.zhuch.exchange.gif

import org.zhuch.exchange.gif.dto.GiphyResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.util.logging.Level

@Component
@ConditionalOnProperty(name = ["fetch.strategy"], havingValue = "webflux")
class GiphyClient(
    httpClient: HttpClient,
    @Value("\${giphy.url}")
    url: String,
    @Value("\${giphy.api-key}")
    private val apiKey: String
) : IGiphyClient {
    private val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .baseUrl(url)
        .build()

    override fun searchGif(query: String, limit: Int, offset: Int): Mono<GiphyResponse> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/search")
                    .queryParam("api_key", apiKey)
                    .queryParam("q", query)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(GiphyResponse::class.java)
            .log(GiphyClient::class.qualifiedName, Level.INFO)
    }
}
