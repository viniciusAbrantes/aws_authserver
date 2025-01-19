package br.pucpr.authserver.integration.sms

import br.pucpr.authserver.users.User
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.SetSMSAttributesRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SmsClient {
    private val sns: AmazonSNSAsync = AmazonSNSAsyncClientBuilder.standard().withRegion(Regions.US_EAST_1)
        .withCredentials(EnvironmentVariableCredentialsProvider()).build()

    fun sendSms(user: User, text: String, important: Boolean = false) {
        if (user.phone.isBlank()) {
            log.warn("userId=${user.id} has no phone number. Ignoring message $text")
            return
        }

        try {
            SetSMSAttributesRequest().apply {
                attributes = mapOf("DefaultSMSType" to if (important) "Transactional" else "Promotional")
                sns.setSMSAttributes(this)
            }
            sns.publishAsync(PublishRequest().apply {
                phoneNumber = user.phone
                message = text
            })

            log.info("Send $text to userId=${user.id}")
        } catch (exception: Exception) {
            log.error("Error publishing sms to userId=$${user.id}, message=$text", exception)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(SmsClient::class.java)
    }
}