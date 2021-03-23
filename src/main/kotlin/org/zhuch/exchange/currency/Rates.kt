package org.zhuch.exchange.currency

import com.fasterxml.jackson.annotation.JsonProperty

import java.util.HashMap

data class Rates(
    @JsonProperty("timestamp")
    var timestamp: Long? = null,
    @JsonProperty("base")
    var base: String? = null,
    @JsonProperty("rates")
    val rates: MutableMap<String, Double> = HashMap(),
)
