package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.revurder.forskudd.data.BidragRevurderForskuddDataTest
import no.nav.bidrag.revurder.forskudd.data.PROFILE_TEST
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.persistence.repository.AktivtVedtakRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@DisplayName("aktivtVedtakServiceTest")
@ActiveProfiles(PROFILE_TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [BidragRevurderForskuddDataTest::class])
class AktivtVedtakServiceTest {

  @Autowired
  private lateinit var aktivtVedtakRepository: AktivtVedtakRepository

  @Autowired
  private lateinit var aktivtVedtakService: AktivtVedtakService

  @Autowired
  private lateinit var persistenceService: PersistenceService

  val vedtakId = 1
  val sakId = "SAK-001"
  val soknadsbarnId = "01010511111"
  val mottakerId = "01018211111"
  val vedtakDatoSisteVedtak = LocalDate.parse("2020-01-01")
  val vedtakDatoSisteManuelleVedtak = LocalDate.parse("2020-01-01")
  val vedtakType = "MANUELL"
  val belop = BigDecimal.valueOf(3490.00)
  val valutakode = "NOK"
  val resultatkode = "FORHOYET_FORSKUDD_100_PROSENT"
  val mottakerSivilstandSisteManuelleVedtak = "GIFT"
  val mottakerAntallBarnSisteManuelleVedtak = 2
  val soknadsbarnBostedsstatus = "MED_FORELDRE"
  val soknadsbarnFodselsdato = LocalDate.parse("2015-01-01")
  val soknadsbarnHarUnntakskode = false
  val opprettetTimestamp = LocalDateTime.now()

  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  @BeforeEach
  fun `init`() {
    // Sletter alle forekomster
    aktivtVedtakRepository.deleteAll()
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal opprette nytt aktivt vedtak`() {
    // Oppretter nytt aktivt vedtak
    val nyttAktivtVedtakOpprettet = aktivtVedtakService.opprettNyttAktivtVedtak(byggAktivtVedtakDto())

    assertThat(nyttAktivtVedtakOpprettet).isNotNull()
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal finne aktivt vedtak fra søknadsbarn id`() {
    // Oppretter nytt aktivt vedtak
    val nyttAktivtVedtakOpprettet = persistenceService.opprettNyttAktivtVedtak(byggAktivtVedtakDto())

    // Finner det aktive vedtaket som akkurat ble opprettet
    val aktivtVedtakFunnet = aktivtVedtakService.finnAktivtVedtak(nyttAktivtVedtakOpprettet.soknadsbarnId)

    assertAll(
      Executable { assertThat(aktivtVedtakFunnet).isNotNull() },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakId).isEqualTo(vedtakId) },
      Executable { assertThat(aktivtVedtakFunnet!!.sakId).isEqualTo(sakId) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnId).isEqualTo(soknadsbarnId) },
      Executable { assertThat(aktivtVedtakFunnet!!.mottakerId).isEqualTo(mottakerId) },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakDatoSisteVedtak).isEqualTo(vedtakDatoSisteVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakDatoSisteManuelleVedtak).isEqualTo(vedtakDatoSisteManuelleVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakType).isEqualTo(vedtakType) },
      Executable { assertThat(aktivtVedtakFunnet!!.belop.compareTo(belop)).isEqualTo(0) },
      Executable { assertThat(aktivtVedtakFunnet!!.valutakode).isEqualTo(valutakode) },
      Executable { assertThat(aktivtVedtakFunnet!!.resultatkode).isEqualTo(resultatkode) },
      Executable { assertThat(aktivtVedtakFunnet!!.mottakerSivilstandSisteManuelleVedtak).isEqualTo(mottakerSivilstandSisteManuelleVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.mottakerAntallBarnSisteManuelleVedtak).isEqualTo(mottakerAntallBarnSisteManuelleVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnBostedsstatus).isEqualTo(soknadsbarnBostedsstatus) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnFodselsdato).isEqualTo(soknadsbarnFodselsdato) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnHarUnntakskode).isEqualTo(soknadsbarnHarUnntakskode) },
      Executable { assertThat(aktivtVedtakFunnet!!.opprettetTimestamp.format(dateTimeFormatter)).isEqualTo(opprettetTimestamp.format(dateTimeFormatter)) }
    )
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal finne aktivt vedtak fra generert id`() {
    // Oppretter nytt aktivt vedtak
    val nyttAktivtVedtakOpprettet = persistenceService.opprettNyttAktivtVedtak(byggAktivtVedtakDto())

    // Finner det aktive vedtaket som akkurat ble opprettet
    val aktivtVedtakFunnet = aktivtVedtakService.finnAktivtVedtakFraId(nyttAktivtVedtakOpprettet.aktivtVedtakId)

    assertAll(
      Executable { assertThat(aktivtVedtakFunnet).isNotNull() },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakId).isEqualTo(vedtakId) },
      Executable { assertThat(aktivtVedtakFunnet!!.sakId).isEqualTo(sakId) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnId).isEqualTo(soknadsbarnId) },
      Executable { assertThat(aktivtVedtakFunnet!!.mottakerId).isEqualTo(mottakerId) },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakDatoSisteVedtak).isEqualTo(vedtakDatoSisteVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakDatoSisteManuelleVedtak).isEqualTo(vedtakDatoSisteManuelleVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.vedtakType).isEqualTo(vedtakType) },
      Executable { assertThat(aktivtVedtakFunnet!!.belop.compareTo(belop)).isEqualTo(0) },
      Executable { assertThat(aktivtVedtakFunnet!!.valutakode).isEqualTo(valutakode) },
      Executable { assertThat(aktivtVedtakFunnet!!.resultatkode).isEqualTo(resultatkode) },
      Executable { assertThat(aktivtVedtakFunnet!!.mottakerSivilstandSisteManuelleVedtak).isEqualTo(mottakerSivilstandSisteManuelleVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.mottakerAntallBarnSisteManuelleVedtak).isEqualTo(mottakerAntallBarnSisteManuelleVedtak) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnBostedsstatus).isEqualTo(soknadsbarnBostedsstatus) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnFodselsdato).isEqualTo(soknadsbarnFodselsdato) },
      Executable { assertThat(aktivtVedtakFunnet!!.soknadsbarnHarUnntakskode).isEqualTo(soknadsbarnHarUnntakskode) },
      Executable { assertThat(aktivtVedtakFunnet!!.opprettetTimestamp.format(dateTimeFormatter)).isEqualTo(opprettetTimestamp.format(dateTimeFormatter)) }
    )
  }

  fun byggAktivtVedtakDto() = AktivtVedtakBo(
    aktivtVedtakId = (1..100).random(),
    vedtakId = vedtakId,
    sakId = sakId,
    soknadsbarnId = soknadsbarnId,
    mottakerId = mottakerId,
    vedtakDatoSisteVedtak = vedtakDatoSisteVedtak,
    vedtakDatoSisteManuelleVedtak = vedtakDatoSisteManuelleVedtak,
    vedtakType = vedtakType,
    belop = belop,
    valutakode = valutakode,
    resultatkode = resultatkode,
    mottakerSivilstandSisteManuelleVedtak = mottakerSivilstandSisteManuelleVedtak,
    mottakerAntallBarnSisteManuelleVedtak = mottakerAntallBarnSisteManuelleVedtak,
    soknadsbarnBostedsstatus = soknadsbarnBostedsstatus,
    soknadsbarnFodselsdato = soknadsbarnFodselsdato,
    soknadsbarnHarUnntakskode = soknadsbarnHarUnntakskode,
    opprettetTimestamp = opprettetTimestamp
  )
}
