package org.zhuch.exchange.gif.dto

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Meta(
    @JsonProperty("status")
    var status: Long? = null,
    @JsonProperty("msg")
    var msg: String? = null,
    @JsonProperty("response_id")
    var responseId: String? = null
)
