package br.pucpr.authserver.files

import br.pucpr.authserver.users.User
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Component
class FileSystemStorage : FileStorage {
    override fun save(user: User, path: String, file: MultipartFile) {
        val destinationFile = Paths.get(ROOT).resolve(path).normalize().toAbsolutePath()
        Files.createDirectories(destinationFile.parent)
        file.inputStream.use {
            Files.copy(it, destinationFile, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    override fun load(path: String): Resource? {
        return Paths.get(ROOT, path.replace("--", "/")).takeIf {
            Files.isRegularFile(it)
        }?.let { UrlResource(it.toUri()) }
    }

    override fun urlFor(name: String): String {
        return BASE_URL + URLEncoder.encode(
            name.replace("/", "--"), StandardCharsets.UTF_8
        )
    }

    companion object {
        private const val ROOT = "./file_system"
        private const val BASE_URL = "http://localhost:8080/api/files/"
    }
}