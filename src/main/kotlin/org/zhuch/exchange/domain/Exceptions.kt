package org.zhuch.exchange.domain

import java.lang.RuntimeException

class RateUnavailable(msg: String): RuntimeException(msg)

class GifImageException(msg: String, ex: Throwable): RuntimeException(msg, ex)
