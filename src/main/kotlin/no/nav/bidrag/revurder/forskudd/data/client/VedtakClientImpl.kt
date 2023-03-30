package no.nav.bidrag.revurder.forskudd.data.client

import no.nav.bidrag.behandling.felles.dto.vedtak.OpprettVedtakRequestDto
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakClient
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakDto
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

private const val BIDRAG_VEDTAK_HENT_VEDTAK_CONTEXT = "/vedtak/%s"

class VedtakClientImpl(private val restTemplate: HttpHeaderRestTemplate) : VedtakClient {

    companion object {
        @JvmStatic
        private val LOGGER: Logger = LoggerFactory.getLogger(VedtakClient::class.java)
    }

    override fun hentVedtak(vedtakId: Int): VedtakDto {
        LOGGER.info("Henter vedtak fra bidrag-vedtak")
        val path = String.format(BIDRAG_VEDTAK_HENT_VEDTAK_CONTEXT, vedtakId)

        val restResponse = restTemplate.exchange(
            path,
            HttpMethod.GET,
            null,
            VedtakDto::class.java
        )

        LOGGER.info("Response: ${restResponse.statusCode}")

        // TODO Håndtere null
        return restResponse.body!!
    }

    override fun opprettVedtak(vedtakRequest: OpprettVedtakRequestDto): Int {
        TODO("Ikke implementert")
    }
}
