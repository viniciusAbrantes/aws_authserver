package br.pucpr.authserver.errors

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(NOT_FOUND)
class NotFoundException(
    message: String = "Not Found", cause: Throwable? = null
) : IllegalArgumentException(message, cause)