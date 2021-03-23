package org.zhuch.exchange.gif.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Pagination(
    @JsonProperty("total_count")
    var totalCount: Long? = null,
    @JsonProperty("count")
    var count: Long? = null,
    @JsonProperty("offset")
    var offset: Long? = null
)
