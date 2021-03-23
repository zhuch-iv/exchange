package org.zhuch.exchange.currency

import feign.Param
import feign.RequestLine

interface FeignExchangeRatesClient {

    @RequestLine("GET /latest.json?app_id={appId}&symbols={symbols}")
    fun latest(@Param("appId") appId: String, @Param("symbols") symbols: String): Rates

    @RequestLine("GET /historical/{date}.json?app_id={appId}&symbols={symbols}")
    fun historical(
        @Param("appId") appId: String,
        @Param("symbols") symbols: String,
        @Param("date") date: String,
    ): Rates
}
