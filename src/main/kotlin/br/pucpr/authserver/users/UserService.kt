package br.pucpr.authserver.users

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.integration.quotes.QuoteClient
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val avatarService: AvatarService,
    val quoteClient: QuoteClient,
    val jwt: Jwt
) {
    fun save(user: User): UserResponse {
        if (user.quote.isBlank()) {
            quoteClient.randomQuote()?.let {
                user.quote = "\"${it.text}\" (${it.author})"
                log.warn("viniabr quote=${user.quote}")
            }
        }

        log.info("User saved: $user")
        return userRepository.save(user).toResponse()
    }

    fun findAll(dir: SortDir, role: String?) = role?.let { r ->
        when (dir) {
            SortDir.ASC -> userRepository.findByRole(r.uppercase()).sortedBy { it.name }
            SortDir.DESC -> userRepository.findByRole(r.uppercase()).sortedByDescending { it.name }
        }
    } ?: when (dir) {
        SortDir.ASC -> userRepository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> userRepository.findAll(Sort.by("name").descending())
    }.map { it.toResponse() }

    fun findByIdOrNull(id: Long) = userRepository.findByIdOrNull(id)?.toResponse()

    fun delete(id: Long) = userRepository.findByIdOrNull(id).also { userRepository.deleteById(id) }

    fun addRole(id: Long, roleName: String): Boolean {
        val user = userRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("User $id not found!")

        if (user.roles.any { it.name == roleName }) return false

        val role = roleRepository.findByName(roleName) ?: throw IllegalArgumentException("Invalid role $roleName!")

        user.roles.add(role)
        userRepository.save(user)
        return true
    }

    fun login(email: String, password: String): LoginResponse? {
        val user = userRepository.findByEmail(email).firstOrNull()

        if (user == null) {
            log.warn("User {} not found!", email)
            return null
        }
        if (password != user.password) {
            log.warn("Invalid password!")
            return null
        }
        log.info("User logged in: id={}, name={}", user.id, user.name)
        return LoginResponse(
            token = jwt.createToken(user), user.toResponse()
        )
    }

    fun saveAvatar(id: Long, avatar: MultipartFile): String {
        val user = userRepository.findByIdOrNull(id) ?: throw NotFoundException("User $id not found!")
        user.avatar = avatarService.save(user, avatar)
        userRepository.save(user)
        return avatarService.urlFor(user.avatar)
    }

    fun User.toResponse() = UserResponse(this, avatarService.urlFor(this.avatar))

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
