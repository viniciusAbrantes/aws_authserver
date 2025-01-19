package br.pucpr.authserver.users.responses

import br.pucpr.authserver.users.User

data class UserResponse(
    val id: Long, val name: String, val quote: String, val uuid: String, val phone: String, val email: String, val avatar: String
) {
    constructor(u: User, avatarUrl: String) : this(
        id = u.id!!, name = u.name, quote = u.quote, uuid = u.uuid, phone = u.phone, email = u.email, avatar = avatarUrl
    )
}
