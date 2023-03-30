package no.nav.bidrag.revurder.forskudd.data.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class JsonMapperService(private val objectMapper: ObjectMapper) {

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(JsonMapperService::class.java)
    }

    fun mapHendelse(hendelse: String): VedtakHendelse {
        return try {
            objectMapper.readValue(hendelse, VedtakHendelse::class.java)
        } finally {
            LOGGER.debug("Leser hendelse: {}", hendelse)
        }
    }

    fun readTree(hendelse: String): JsonNode = objectMapper.readTree(hendelse)
}
