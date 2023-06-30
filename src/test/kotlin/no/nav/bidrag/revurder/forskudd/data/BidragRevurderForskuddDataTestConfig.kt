package no.nav.bidrag.revurder.forskudd.data

import com.github.tomakehurst.wiremock.core.Options
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate
import no.nav.bidrag.revurder.forskudd.data.hendelse.PojoVedtakHendelseListener
import no.nav.bidrag.revurder.forskudd.data.hendelse.VedtakHendelseListener
import no.nav.bidrag.revurder.forskudd.data.service.BehandleHendelseService
import no.nav.bidrag.revurder.forskudd.data.service.JsonMapperService
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

const val PROFILE_TEST = "test"

@Configuration
@Profile(PROFILE_TEST)
class BidragRevurderForskuddDataTestConfig {

    @Bean
    fun testRestTemplate(testRestTemplate: TestRestTemplate): HttpHeaderTestRestTemplate? {
        return HttpHeaderTestRestTemplate(testRestTemplate)
    }

    @Bean
    fun vedtakHendelseListener(
        jsonMapperService: JsonMapperService,
        behandeHendelseService: BehandleHendelseService
    ): VedtakHendelseListener = PojoVedtakHendelseListener(jsonMapperService, behandeHendelseService)

    @Bean
    fun wireMockOptions(): Options? {
        val options = WireMockSpring.options()
        options.port(8096)
        return options
    }
}
