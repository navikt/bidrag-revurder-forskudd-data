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

    val vedtakId = 1
    val vedtakType = VedtakType.MANUELT
    val stonadType = StonadType.FORSKUDD
    val sakId = "SAK-001"
    val skyldnerId = "13579"
    val kravhaverId = "54321"
    val mottakerId = "24680"
    val opprettetAv = "TEST"
    val opprettetTimestamp = LocalDateTime.now()
    val periodeFom = LocalDate.now()
    val belop = BigDecimal.valueOf(100)
    val valutakode = "NOK"
    val resultatkode = "RESULTATKODE"

    vedtakHendelseListener.lesHendelse(
      """
            {
              "vedtakId": $vedtakId,
              "vedtakType": "$vedtakType",
              "stonadType": "$stonadType",
              "sakId": "$sakId",
              "skyldnerId": "$skyldnerId",
              "kravhaverId": "$kravhaverId",
              "mottakerId": "$mottakerId",
              "opprettetAv": "$opprettetAv",
              "opprettetTimestamp": "$opprettetTimestamp",
              "periodeListe":[
                {
                  "periodeFom": "$periodeFom",
                  "periodeTil": null,
                  "belop": $belop,
                  "valutakode": "$valutakode",
                  "resultatkode": "$resultatkode"
                }
              ]
            }
            """.trimIndent()
    )

    //TODO Sjekke på sporingsdata?
    verify(behandleHendelseServiceMock).behandleHendelse(
      VedtakHendelse(
        vedtakId = vedtakId,
        vedtakType = vedtakType,
        stonadType = stonadType,
        sakId = sakId,
        skyldnerId = skyldnerId,
        kravhaverId = kravhaverId,
        mottakerId = mottakerId,
        opprettetAv = opprettetAv,
        opprettetTimestamp = opprettetTimestamp,
        periodeListe = listOf(
          VedtakHendelsePeriode(
            periodeFom = periodeFom,
            periodeTil = null,
            belop = belop,
            valutakode = valutakode,
            resultatkode = resultatkode
          )
        )
      )
    )
  }
}
