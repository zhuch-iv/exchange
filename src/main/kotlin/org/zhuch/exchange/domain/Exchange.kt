package org.zhuch.exchange.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

data class Exchange(
    val yesterday: Double,
    val latest: Double,
    val gifUrl: String = "",
) {
    @get:JsonIgnore
    val type: Type = if (latest > yesterday) { Type.RICH } else { Type.BROKE }

    fun getQuery(): String {
        return type.value
    }

    enum class Type(
        @get:JsonValue
        val value: String
    ) {
        RICH("rich"), BROKE("broke")
    }
}
