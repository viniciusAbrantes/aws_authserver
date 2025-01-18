package br.pucpr.authserver.files

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.users.AvatarService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class FilesController(private val avatarService: AvatarService) {
    @GetMapping("/{filename}")
    fun serve(@PathVariable filename: String): ResponseEntity<Resource> {
        return avatarService.load(filename)?.let {
            val contentType = if (filename.endsWith("png")) MediaType.IMAGE_PNG else MediaType.IMAGE_JPEG
            ResponseEntity.ok().contentType(contentType).body(it)
        } ?: throw NotFoundException()
    }
}