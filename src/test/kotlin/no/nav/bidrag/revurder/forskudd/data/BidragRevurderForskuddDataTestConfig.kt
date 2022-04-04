package no.nav.bidrag.revurder.forskudd.data

import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate
import no.nav.bidrag.revurder.forskudd.data.hendelse.PojoVedtakHendelseListener
import no.nav.bidrag.revurder.forskudd.data.hendelse.VedtakHendelseListener
import no.nav.bidrag.revurder.forskudd.data.service.BehandleHendelseService
import no.nav.bidrag.revurder.forskudd.data.service.JsonMapperService
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

const val PROFILE_TEST = "test"

@Configuration
@Profile(PROFILE_TEST)
class BidragRevurderForskuddDataTestConfig {

  @Bean
  fun testRestTemplate(testRestTemplate: TestRestTemplate?): HttpHeaderTestRestTemplate? {
    val httpHeaderTestRestTemplate = HttpHeaderTestRestTemplate(testRestTemplate)
    return httpHeaderTestRestTemplate
  }

  @Bean
  fun vedtakHendelseListener(
    jsonMapperService: JsonMapperService,
    behandeHendelseService: BehandleHendelseService
  ): VedtakHendelseListener = PojoVedtakHendelseListener(jsonMapperService, behandeHendelseService)
}
