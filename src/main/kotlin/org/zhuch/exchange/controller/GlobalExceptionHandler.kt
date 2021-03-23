package org.zhuch.exchange.controller

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.zhuch.exchange.domain.GifImageException
import org.zhuch.exchange.domain.RateUnavailable
import org.springframework.web.bind.annotation.RestControllerAdvice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [RateUnavailable::class])
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun rateUnavailableException(ex: RateUnavailable): ErrorMessage {
        return ErrorMessage(404, ex.message!!)
    }

    @ExceptionHandler(value = [GifImageException::class])
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun gifUnavailableException(ex: GifImageException): ErrorMessage {
        return ErrorMessage(500, ex.toString(), ex.cause!!.message.toString())
    }
}

data class ErrorMessage(val statusCode: Int, val message: String, val cause: String? = null) {
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @get:JsonSerialize(using = LocalDateTimeSerializer::class)
    val timestamp: LocalDateTime = LocalDateTime.now()
}
