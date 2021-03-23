package org.zhuch.exchange.domain

import org.zhuch.exchange.currency.IExchangeRatesClient
import org.zhuch.exchange.currency.Rates
import org.zhuch.exchange.gif.GifLoader
import org.zhuch.exchange.gif.IGiphyClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.lang.Exception
import java.time.LocalDate
import kotlin.random.Random

@Service
class ExchangeService(
    private val exchangeRatesClient: IExchangeRatesClient,
    private val giphyClient: IGiphyClient,
    private val gifLoader: GifLoader,
) {

    fun getGif(currency: String): Mono<ByteArray> {
        return getExchange(currency)
            .flatMap { gifLoader.loadGif(it.gifUrl) }
    }

    fun getExchange(currency: String): Mono<Exchange> {
        return fetchExchange(currency)
            .flatMap(this::getGifUrl)
    }

    private fun fetchExchange(currency: String): Mono<Exchange> {
        if (currency == RUB) {
            return Mono.just(Exchange(1.0, 1.0))
        }
        return getLastRate(currency)
            .zipWith(getYesterdayRate(currency)) { last, old -> Exchange(yesterday = old, latest = last) }
    }

    private fun getLastRate(currency: String): Mono<Double> {
        return exchangeRatesClient.latest(listOf(currency, RUB))
            .map { mapRates(it, currency) }
    }

    private fun getYesterdayRate(currency: String): Mono<Double> {
        return exchangeRatesClient.historical(listOf(currency, RUB), LocalDate.now().minusDays(1))
            .map { mapRates(it, currency) }
    }

    private fun getGifUrl(currency: Exchange): Mono<Exchange> {
        return giphyClient.searchGif(query = currency.getQuery(), limit = 1, offset = getRandomNumber())
            .map {
                try {
                    return@map currency.copy(gifUrl = it.data?.get(0)!!.images!!.downsized!!.url!!)
                } catch (ex: Exception) {
                    throw GifImageException("Search gif parse exception", ex)
                }
            }
    }

    private fun mapRates(r: Rates, currency: String): Double {
        val rub = r.rates[RUB]
        val curr = r.rates[currency]
        if (rub == null || curr == null) {
            val unavailable = if (rub == null) { RUB } else { currency }
            throw RateUnavailable("Currency with ticker '$unavailable' not available")
        }
        return (curr * rub)
    }

    private fun getRandomNumber(): Int {
        return Random.nextInt(0, randomLimit)
    }

    companion object {
        const val RUB = "RUB"
        private const val randomLimit = 700
    }
}
