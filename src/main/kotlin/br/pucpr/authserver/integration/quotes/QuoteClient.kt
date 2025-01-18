package br.pucpr.authserver.integration.quotes

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Component
class QuoteClient {
    fun randomQuote(): Quote? {
        return try {
            RestTemplate().getForObject<QuoteResponse?>(RANDOM_QUOTE_URL, QuoteResponse::class)?.data?.firstOrNull()
        } catch (error: RestClientException) {
            log.error("Problem accessing the quote service: ", error)
            null
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(QuoteClient::class.java)
        private const val RANDOM_QUOTE_URL = "https://quotegarden.onrender.com/api/v3/quotes/random"
    }
}