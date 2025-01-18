package br.pucpr.authserver.files

import br.pucpr.authserver.users.User
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class S3Storage : FileStorage {
    private val s3: AmazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
        .withCredentials(EnvironmentVariableCredentialsProvider()).build()

    override fun save(user: User, path: String, file: MultipartFile) {
        val metadata = ObjectMetadata().apply {
            contentType = file.contentType
            contentLength = file.size
            userMetadata["userId"] = "${user.id}"
            userMetadata["originalFilename"] = file.originalFilename
        }

        val transferManager = TransferManagerBuilder.standard().withS3Client(s3).build()
        transferManager.upload(BUCKET, path, file.inputStream, metadata).waitForUploadResult()
    }

    override fun load(path: String): Resource {
        return InputStreamResource(
            s3.getObject(BUCKET, path.replace("--", "/")).objectContent
        )
    }

    override fun urlFor(name: String): String {
        return "$URL_PREFIX/$name"
    }

    companion object {
        private const val BUCKET = "authserver-public-727646483783"
        private const val URL_PREFIX = "https://d3fwde429r4bot.cloudfront.net"
    }
}