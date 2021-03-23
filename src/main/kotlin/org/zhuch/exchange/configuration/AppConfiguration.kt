package org.zhuch.exchange.configuration

import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.Feign
import feign.Logger
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.zhuch.exchange.currency.FeignExchangeRatesClient
import org.zhuch.exchange.gif.FeignGiphyClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class AppConfiguration {

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder
        = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule())

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient.create()
            .doOnConnected {
                it.addHandlerLast(ReadTimeoutHandler(5, TimeUnit.SECONDS))
                    .addHandlerLast(WriteTimeoutHandler(5, TimeUnit.SECONDS))
            }
    }

    @Bean
    @ConditionalOnProperty(name = ["fetch.strategy"], havingValue = "feign")
    fun feignGiphyClient(@Value("\${giphy.url}") url: String): FeignGiphyClient {
        return Feign.builder()
            .client(OkHttpClient())
            .encoder(GsonEncoder())
            .decoder(GsonDecoder())
            .logger(Slf4jLogger(FeignGiphyClient::class.java))
            .logLevel(Logger.Level.FULL)
            .target(FeignGiphyClient::class.java, url)
    }

    @Bean
    @ConditionalOnProperty(name = ["fetch.strategy"], havingValue = "feign")
    fun feignExchangeRatesClient(@Value("\${open-exchange-rates.url}") url: String): FeignExchangeRatesClient {
        return Feign.builder()
            .client(OkHttpClient())
            .encoder(GsonEncoder())
            .decoder(GsonDecoder())
            .logger(Slf4jLogger(FeignExchangeRatesClient::class.java))
            .logLevel(Logger.Level.FULL)
            .target(FeignExchangeRatesClient::class.java, url)
    }
}
