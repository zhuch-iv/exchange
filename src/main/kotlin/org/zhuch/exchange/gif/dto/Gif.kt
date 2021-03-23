package org.zhuch.exchange.gif.dto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Gif(
    @JsonProperty("type")
    var type: String? = null,
    @JsonProperty("id")
    var id: String? = null,
    @JsonProperty("url")
    var url: String? = null,
    @JsonProperty("slug")
    var slug: String? = null,
    @JsonProperty("bitly_gif_url")
    var bitlyGifUrl: String? = null,
    @JsonProperty("bitly_url")
    var bitlyUrl: String? = null,
    @JsonProperty("images")
    var images: Images? = null,
)
