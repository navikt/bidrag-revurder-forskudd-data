package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.StonadType
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.BidragRevurderForskuddDataTest
import no.nav.bidrag.revurder.forskudd.data.PROFILE_TEST
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelsePeriode
import no.nav.bidrag.revurder.forskudd.data.persistence.repository.AktivtVedtakRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("BehandleHendelseServiceTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [BidragRevurderForskuddDataTest::class])
@AutoConfigureWireMock(port = 8096)
@ActiveProfiles(PROFILE_TEST)
internal class BehandleHendelseServiceTest {

  @Autowired
  private lateinit var behandleHendelseService: DefaultBehandleHendelseService

  @Autowired
  private lateinit var persistenceService: PersistenceService

  @Autowired
  private lateinit var aktivtVedtakRepository: AktivtVedtakRepository

  @BeforeEach
  fun `init`() {
    // Sletter alle forekomster
    aktivtVedtakRepository. deleteAll()
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal opprette nytt aktivt vedtak basert på data fra VedtakHendelse`() {

    // Oppretter ny hendelse
    val nyHendelse = VedtakHendelse(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      stonadType = StonadType.FORSKUDD,
      sakId = sakId,
      skyldnerId = "12345",
      kravhaverId = kravhaverId,
      mottakerId = mottakerId,
      opprettetAv = "",
      opprettetTimestamp = dateTimeNow,
      periodeListe = listOf(
        VedtakHendelsePeriode(
          periodeFom = dateNow.minusYears(1).withDayOfMonth(1),
          periodeTil = dateNow.withDayOfMonth(1),
          belop = belop2,
          valutakode = valutakode2,
          resultatkode = resultatkode2
        ),
        VedtakHendelsePeriode(
          periodeFom = dateNow.withDayOfMonth(1),
          periodeTil = null,
          belop = belop1,
          valutakode = valutakode1,
          resultatkode = resultatkode1
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    // Sjekker at nytt aktivt vedtak har blitt opprettet
    val finnAktivtVedtakOpprettetBasertPaaKravhaver = persistenceService.finnAktivtVedtak(kravhaverId)
    // Finner aktivt vedtak basert på id
    val finnAktivtVedtakOpprettetBasertPaaId = persistenceService.finnAktivtVedtakFraId(finnAktivtVedtakOpprettetBasertPaaKravhaver!!.aktivtVedtakId)

    assertAll(
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver).isNotNull() },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaId).isNotNull() },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver).isEqualTo(finnAktivtVedtakOpprettetBasertPaaId) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.vedtakId).`as`("vedtakId").isEqualTo(vedtakId) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.sakId).`as`("sakId").isEqualTo(sakId) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.soknadsbarnId).`as`("soknadsbarnId").isEqualTo(kravhaverId) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.mottakerId).`as`("mottakerId").isEqualTo(mottakerId) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.vedtakDatoSisteVedtak).`as`("vedtakDatoSisteVedtak").isEqualTo(dateNow) },
      Executable {
        assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.vedtakDatoSisteManuelleVedtak).`as`("vedtakDatoSisteManuelleVedtak").isEqualTo(dateNow)
      },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.belop.compareTo(belop1)).`as`("belop").isEqualTo(0) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.valutakode).`as`("valutakode").isEqualTo(valutakode1) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.resultatkode).`as`("resultatkode").isEqualTo(resultatkode1) },
      Executable {
        assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.mottakerSivilstandSisteManuelleVedtak).`as`("mottakerSivilstandSisteManuelleVedtak")
          .isEqualTo(sivilstandkode1)
      },
      Executable {
        assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.mottakerAntallBarnSisteManuelleVedtak).`as`("mottakerAntallBarnSisteManuelleVedtak")
          .isEqualTo(BidragVedtakData().mottakerAntallBarnSisteManuelleVedtak)
      },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.soknadsbarnBostedsstatus).`as`("soknadsbarnBostedsstatus").isEqualTo(bostatuskode1) },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.soknadsbarnFodselsdato).`as`("soknadsbarnFodselsdato").isEqualTo(fodselsdato) },
      Executable {
        assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.soknadsbarnHarUnntakskode).`as`("soknadsbarnHarUnntakskode")
          .isEqualTo(BidragVedtakData().soknadsbarnHarUnntakskode)
      },
      Executable {
        assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.opprettetTimestamp.toLocalDate()).`as`("opprettetTimestamp").isEqualTo(LocalDate.now())
      },
      Executable { assertThat(finnAktivtVedtakOpprettetBasertPaaKravhaver.sistEndretTimestamp).`as`("soknadsbarnFodselsdato").isNull() }
    )
  }

  private companion object {

    val vedtakId = 1
    val vedtakType = VedtakType.MANUELT
    val sakId = "SAK-001"
    val kravhaverId = "54321"
    val mottakerId = "24680"
    val belop1 = BigDecimal.valueOf(100)
    val valutakode1 = "NOK"
    val resultatkode1 = "RESULTATKODE1"
    val dateNow = LocalDate.now()
    val dateTimeNow = LocalDateTime.now()
    val sivilstandkode1 = SivilstandKode.GIFT
    val bostatuskode1 = BostatusKode.MED_FORELDRE
    val fodselsdato = "2006-02-01"

    val belop2 = BigDecimal.valueOf(200)
    val valutakode2 = "EUR"
    val resultatkode2 = "RESULTATKODE2"
  }
}
