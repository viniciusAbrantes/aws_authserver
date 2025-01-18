package br.pucpr.authserver.errors

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
class UnsupportedMediaTypeException(
    message: String = "Unsupported media type", cause: Throwable? = null
) : IllegalArgumentException(message, cause) {
    public constructor(
        vararg types: String, cause: Throwable?
    ) : this("Unsupported Media Type. Supported types: {${types.joinToString()}}", cause)
}