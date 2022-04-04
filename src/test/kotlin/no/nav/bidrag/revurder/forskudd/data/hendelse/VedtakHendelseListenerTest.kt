package no.nav.bidrag.revurder.forskudd.data.hendelse

import no.nav.bidrag.behandling.felles.enums.StonadType
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.BidragRevurderForskuddDataTest
import no.nav.bidrag.revurder.forskudd.data.PROFILE_TEST
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelsePeriode
import no.nav.bidrag.revurder.forskudd.data.service.BehandleHendelseService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("VedtakHendelseListener (test av forretningslogikk)")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [BidragRevurderForskuddDataTest::class])
@ActiveProfiles(PROFILE_TEST)
internal class VedtakHendelseListenerTest {

  @Autowired
  private lateinit var vedtakHendelseListener: VedtakHendelseListener

  @MockBean
  private lateinit var behandleHendelseServiceMock: BehandleHendelseService

  @Test
  fun `skal lese og behandle vedtakshendelse`() {
    vedtakHendelseListener.lesHendelse(
      """
            {
              "vedtakId":1,
              "vedtakType":"MANUELT",
              "stonadType":"FORSKUDD",
              "sakId":"1",
              "skyldnerId":"1",
              "kravhaverId":"1",
              "mottakerId":"1",
              "opprettetAv":"TEST",
              "opprettetTimestamp":"2022-01-11T10:00:00.000001",
              "periodeListe":[
                {
                  "periodeFom":"2021-01-01",
                  "periodeTil":"2022-01-01",
                  "belop":0,
                  "valutakode":"NOK",
                  "resultatkode":"AVSLAG"
                }
              ]
            }
            """.trimIndent()
    )

    //TODO Sjekke på sporingsdata?
    verify(behandleHendelseServiceMock).behandleHendelse(
      VedtakHendelse(
        vedtakId = 1,
        vedtakType = VedtakType.MANUELT,
        stonadType = StonadType.FORSKUDD,
        sakId = "1",
        skyldnerId = "1",
        kravhaverId = "1",
        mottakerId = "1",
        opprettetAv = "TEST",
        opprettetTimestamp = LocalDateTime.parse("2022-01-11T10:00:00.000001"),
        periodeListe = listOf(
          VedtakHendelsePeriode(
            periodeFom = LocalDate.parse("2021-01-01"),
            periodeTil = LocalDate.parse("2022-01-01"),
            belop = BigDecimal.ZERO,
            valutakode = "NOK",
            resultatkode = "AVSLAG"
          )
        )
      )
    )
  }
}
