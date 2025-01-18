package br.pucpr.authserver.users

import br.pucpr.authserver.errors.UnsupportedMediaTypeException
import br.pucpr.authserver.files.FileStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AvatarService(
    @Qualifier("fileStorage")
    val storage: FileStorage) {
    fun save(user: User, avatar: MultipartFile): String {
        try {
            val extension = when (avatar.contentType) {
                "image/jpeg" -> "jpeg"
                "image/png" -> "png"
                "image/jpg" -> "jpg"
                else -> throw UnsupportedMediaTypeException("jpeg")
            }

            val path = "${user.id}/a_${user.id}.${extension}"
            storage.save(user, "$ROOT/$path", avatar)
            return path
        } catch (e: Exception) {
            log.error("Unable to store user ${user.id} avatar. Using default")
            return DEFAULT_AVATAR
        }
    }

    fun load(path: String) = storage.load(path)
    fun urlFor(name: String) = storage.urlFor("$ROOT/$name")

    companion object {
        private const val ROOT = "avatars"
        const val DEFAULT_AVATAR = "default.jpg"
        private val log = LoggerFactory.getLogger(AvatarService::class.java)
    }
}