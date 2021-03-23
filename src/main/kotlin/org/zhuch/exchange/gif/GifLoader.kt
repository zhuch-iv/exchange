package org.zhuch.exchange.gif

import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Component
class GifLoader(
    httpClient: HttpClient
) {
    private val webClient: WebClient = WebClient.builder()
        .codecs {
            it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
        }
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()

    fun loadGif(url: String): Mono<ByteArray> {
        return webClient.get()
            .uri(url)
            .accept(MediaType.IMAGE_GIF)
            .retrieve()
            .bodyToMono(ByteArray::class.java)
    }
}
