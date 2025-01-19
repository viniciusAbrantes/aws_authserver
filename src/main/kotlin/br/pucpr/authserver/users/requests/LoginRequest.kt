package br.pucpr.authserver.users.requests

data class LoginRequest(
    val phone: String?,
    val uuid: String?
)
