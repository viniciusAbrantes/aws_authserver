package br.pucpr.authserver.files

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class FileConfig {
    @Bean("fileStorage")
    @Profile("file_system")
    fun localStorage() = FileSystemStorage()

    @Bean("fileStorage")
    @Profile("!file_system")
    fun s3Storage() = S3Storage()
}