package org.zhuch.exchange.gif

import org.zhuch.exchange.gif.dto.GiphyResponse
import reactor.core.publisher.Mono

interface IGiphyClient {

    fun searchGif(query: String, limit: Int, offset: Int): Mono<GiphyResponse>
}
