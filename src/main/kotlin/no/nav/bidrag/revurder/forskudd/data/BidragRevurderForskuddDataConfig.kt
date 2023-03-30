package no.nav.bidrag.revurder.forskudd.data

import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakClient
import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.commons.security.api.EnableSecurityConfiguration
import no.nav.bidrag.commons.security.service.SecurityTokenService
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.revurder.forskudd.data.client.VedtakClientImpl
import no.nav.bidrag.revurder.forskudd.data.hendelse.KafkaVedtakHendelseListener
import no.nav.bidrag.revurder.forskudd.data.service.BehandleHendelseService
import no.nav.bidrag.revurder.forskudd.data.service.JsonMapperService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RootUriTemplateHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.kafka.listener.KafkaListenerErrorHandler
import org.springframework.kafka.listener.ListenerExecutionFailedException
import org.springframework.messaging.Message
import java.util.*

const val LIVE_PROFILE = "live"

@Configuration
@EnableSecurityConfiguration
class BidragRevurderForskuddDataConfig {

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(BidragRevurderForskuddDataConfig::class.java)
    }

    @Bean
    fun exceptionLogger(): ExceptionLogger {
        return ExceptionLogger(BidragRevurderForskuddData::class.java.simpleName)
    }

    @Bean
    fun correlationIdFilter(): CorrelationIdFilter {
        return CorrelationIdFilter()
    }

    @Bean
    @Scope("prototype")
    fun restTemplate(): HttpHeaderRestTemplate {
        val httpHeaderRestTemplate = HttpHeaderRestTemplate(HttpComponentsClientHttpRequestFactory())
        httpHeaderRestTemplate.addHeaderGenerator(CorrelationIdFilter.CORRELATION_ID_HEADER) { CorrelationId.fetchCorrelationIdForThread() }
        return httpHeaderRestTemplate
    }

    @Bean
    fun vedtakClient(
        @Value("\${VEDTAK_URL}") url: String,
        restTemplate: HttpHeaderRestTemplate,
        securityTokenService: SecurityTokenService,
        exceptionLogger: ExceptionLogger
    ): VedtakClient {
        LOGGER.info("Url satt i config: $url")
        restTemplate.uriTemplateHandler = RootUriTemplateHandler(url)
        restTemplate.interceptors.add(securityTokenService.serviceUserAuthTokenInterceptor("bidragvedtak"))
        return VedtakClientImpl(restTemplate)
    }
}

@Configuration
@Profile(LIVE_PROFILE)
class KafkaConfig {

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(KafkaConfig::class.java)
    }

    @Bean
    fun vedtakHendelseListener(
        jsonMapperService: JsonMapperService,
        behandeHendelseService: BehandleHendelseService
    ) = KafkaVedtakHendelseListener(jsonMapperService, behandeHendelseService)

    @Bean
    fun vedtakshendelseErrorHandler(): KafkaListenerErrorHandler {
        return KafkaListenerErrorHandler { message: Message<*>, e: ListenerExecutionFailedException ->
            val messagePayload: Any = try {
                message.payload
            } catch (re: RuntimeException) {
                "Unable to read message payload"
            }

            LOGGER.error("Message {} cause error: {} - {} - headers: {}", messagePayload, e.javaClass.simpleName, e.message, message.headers)
            Optional.empty<Any>()
        }
    }
}
