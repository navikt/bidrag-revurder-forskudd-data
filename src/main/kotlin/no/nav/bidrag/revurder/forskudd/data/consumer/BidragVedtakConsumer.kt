package no.nav.bidrag.revurder.forskudd.data.consumer

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.revurder.forskudd.data.consumer.api.HentVedtakResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

private const val BIDRAG_VEDTAK_HENT_VEDTAK_CONTEXT = "/vedtak/%s"

interface BidragVedtakConsumer {
  fun hentVedtak(vedtakId: Int): HentVedtakResponse
}

class DefaultBidragVedtakConsumer(private val restTemplate: HttpHeaderRestTemplate) : BidragVedtakConsumer {

  companion object {
    @JvmStatic
    private val LOGGER: Logger = LoggerFactory.getLogger(BidragVedtakConsumer::class.java)
  }

  override fun hentVedtak(vedtakId: Int): HentVedtakResponse {
    LOGGER.info("Henter vedtak fra bidrag-vedtak")
    val path = String.format(BIDRAG_VEDTAK_HENT_VEDTAK_CONTEXT, vedtakId)

    val restResponse = restTemplate.exchange(
      path,
      HttpMethod.GET,
      null,
      HentVedtakResponse::class.java
    )

    LOGGER.info("Response: ${restResponse.statusCode}")

    //TODO Håndtere null
    return restResponse.body!!
  }
}
