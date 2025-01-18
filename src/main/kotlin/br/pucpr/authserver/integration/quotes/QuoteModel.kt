package br.pucpr.authserver.integration.quotes

import com.fasterxml.jackson.annotation.JsonProperty

data class Quote(
    @JsonProperty("quoteText")
    val text: String,

    @JsonProperty("quoteAuthor")
    val author: String
)

data class QuoteResponse(val data: List<Quote>)