package br.pucpr.authserver.users.requests

data class ConfirmationRequest(
    val phone: String?,
    val uuid: String?,
    val code: Long
)
