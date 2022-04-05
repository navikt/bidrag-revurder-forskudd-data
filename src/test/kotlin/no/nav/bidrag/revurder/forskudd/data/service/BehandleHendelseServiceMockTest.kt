package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.behandling.felles.dto.vedtak.GrunnlagDto
import no.nav.bidrag.behandling.felles.dto.vedtak.StonadsendringDto
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakClient
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakDto
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakPeriodeDto
import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.GrunnlagType
import no.nav.bidrag.behandling.felles.enums.Rolle
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.StonadType
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.dto.FinnAktivtVedtakDto
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelsePeriode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("BehandleHendelseServiceMockTest")
@ExtendWith(MockitoExtension::class)
internal class BehandleHendelseServiceMockTest {

  @InjectMocks
  private lateinit var behandleHendelseService: DefaultBehandleHendelseService

  @Mock
  private lateinit var aktivtVedtakServiceMock: AktivtVedtakService

  @Mock
  private lateinit var vedtakClientMock: VedtakClient

  @Captor
  private lateinit var aktivtVedtakCaptor: ArgumentCaptor<AktivtVedtakBo>

  @Captor
  private lateinit var slettAktivtVedtakCaptor: ArgumentCaptor<Int>

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal opprette nytt aktivt vedtak basert på data fra VedtakHendelse`() {

    whenever(aktivtVedtakServiceMock.finnAktivtVedtak(any())).thenReturn(null)
    whenever(aktivtVedtakServiceMock.opprettNyttAktivtVedtak(MockitoHelper.capture(aktivtVedtakCaptor))).thenReturn(1)

    // Simulerer vedtak returnert fra bidrag-vedtak
    whenever(vedtakClientMock.hentVedtak(any())).thenReturn(lagVedtakDtoNytt())

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

    // Henter nytt aktivt vedtak som ville vært input til aktivtVedtakService.opprettNyttAktivtVedtak(nyttAktivtVedtak)
    val nyttAktivtVedtakSomSkalOpprettes = aktivtVedtakCaptor.value

    verify(this.aktivtVedtakServiceMock, times(1)).opprettNyttAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).oppdaterAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).slettAktivtVedtak(any())

    assertAll(
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes).isNotNull() },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.vedtakId).`as`("vedtakId").isEqualTo(vedtakId) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.sakId).`as`("sakId").isEqualTo(sakId) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnId).`as`("soknadsbarnId").isEqualTo(kravhaverId) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.mottakerId).`as`("mottakerId").isEqualTo(mottakerId) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.vedtakDatoSisteVedtak).`as`("vedtakDatoSisteVedtak").isEqualTo(dateNow) },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.vedtakDatoSisteManuelleVedtak).`as`("vedtakDatoSisteManuelleVedtak").isEqualTo(dateNow)
      },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.belop).`as`("belop").isEqualTo(belop1) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.valutakode).`as`("valutakode").isEqualTo(valutakode1) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.resultatkode).`as`("resultatkode").isEqualTo(resultatkode1) },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.mottakerSivilstandSisteManuelleVedtak).`as`("mottakerSivilstandSisteManuelleVedtak")
          .isEqualTo(sivilstandkode1)
      },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.mottakerAntallBarnSisteManuelleVedtak).`as`("mottakerAntallBarnSisteManuelleVedtak")
          .isEqualTo(BidragVedtakData().mottakerAntallBarnSisteManuelleVedtak)
      },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnBostedsstatus).`as`("soknadsbarnBostedsstatus").isEqualTo(bostatuskode1) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnFodselsdato).`as`("soknadsbarnFodselsdato").isEqualTo(fodselsdato) },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnHarUnntakskode).`as`("soknadsbarnHarUnntakskode")
          .isEqualTo(BidragVedtakData().soknadsbarnHarUnntakskode)
      },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.opprettetTimestamp.toLocalDate()).`as`("opprettetTimestamp").isEqualTo(LocalDate.now())
      },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.sistEndretTimestamp).`as`("soknadsbarnFodselsdato").isNull() }
    )
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal oppdatere aktivt vedtak basert på data fra VedtakHendelse`() {

    whenever(aktivtVedtakServiceMock.finnAktivtVedtak(any())).thenReturn(lagFinnAktivtVedtakDto())
    whenever(aktivtVedtakServiceMock.oppdaterAktivtVedtak(MockitoHelper.capture(aktivtVedtakCaptor))).thenReturn(1)

    // Simulerer vedtak returnert fra bidrag-vedtak
    whenever(vedtakClientMock.hentVedtak(any())).thenReturn(lagVedtakDtoOppdatert())

    // Oppretter ny hendelse
    val nyHendelse = VedtakHendelse(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      stonadType = StonadType.FORSKUDD,
      sakId = sakId,
      skyldnerId = "12345",
      kravhaverId = kravhaverId,
      mottakerId = mottakerId,
      opprettetAv = "TEST",
      opprettetTimestamp = dateTimeNow,
      periodeListe = listOf(
        VedtakHendelsePeriode(
          periodeFom = dateNow.minusYears(1).withDayOfMonth(1),
          periodeTil = dateNow.withDayOfMonth(1),
          belop = belop1,
          valutakode = valutakode1,
          resultatkode = resultatkode1
        ),
        VedtakHendelsePeriode(
          periodeFom = dateNow.withDayOfMonth(1),
          periodeTil = null,
          belop = belop2,
          valutakode = valutakode2,
          resultatkode = resultatkode2
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    // Henter nytt aktivt vedtak som ville vært input til aktivtVedtakService.oppdaterEksisterendeAktivtVedtak(oppdatertAktivtVedtak)
    val aktivtVedtakSomSkalOppdateres = aktivtVedtakCaptor.value

    verify(this.aktivtVedtakServiceMock, times(0)).opprettNyttAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(1)).oppdaterAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).slettAktivtVedtak(any())

    assertAll(
      Executable { assertThat(aktivtVedtakSomSkalOppdateres).isNotNull() },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.vedtakId).`as`("vedtakId").isEqualTo(vedtakId) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.sakId).`as`("sakId").isEqualTo(sakId) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.soknadsbarnId).`as`("soknadsbarnId").isEqualTo(kravhaverId) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.mottakerId).`as`("mottakerId").isEqualTo(mottakerId) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.vedtakDatoSisteVedtak).`as`("vedtakDatoSisteVedtak").isEqualTo(dateNow) },
      Executable {
        assertThat(aktivtVedtakSomSkalOppdateres.vedtakDatoSisteManuelleVedtak).`as`("vedtakDatoSisteManuelleVedtak").isEqualTo(dateNow)
      },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.belop).`as`("belop").isEqualTo(belop2) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.valutakode).`as`("valutakode").isEqualTo(valutakode2) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.resultatkode).`as`("resultatkode").isEqualTo(resultatkode2) },
      Executable {
        assertThat(aktivtVedtakSomSkalOppdateres.mottakerSivilstandSisteManuelleVedtak).`as`("mottakerSivilstandSisteManuelleVedtak")
          .isEqualTo(sivilstandkode2)
      },
      Executable {
        assertThat(aktivtVedtakSomSkalOppdateres.mottakerAntallBarnSisteManuelleVedtak).`as`("mottakerAntallBarnSisteManuelleVedtak")
          .isEqualTo(BidragVedtakData().mottakerAntallBarnSisteManuelleVedtak)
      },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.soknadsbarnBostedsstatus).`as`("soknadsbarnBostedsstatus").isEqualTo(bostatuskode2) },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.soknadsbarnFodselsdato).`as`("soknadsbarnFodselsdato").isEqualTo(fodselsdato) },
      Executable {
        assertThat(aktivtVedtakSomSkalOppdateres.soknadsbarnHarUnntakskode).`as`("soknadsbarnHarUnntakskode")
          .isEqualTo(BidragVedtakData().soknadsbarnHarUnntakskode)
      },
      Executable {
        assertThat(aktivtVedtakSomSkalOppdateres.opprettetTimestamp).`as`("opprettetTimestamp").isEqualTo(dateTimeNow.minusYears(1))
      },
      Executable { assertThat(aktivtVedtakSomSkalOppdateres.sistEndretTimestamp!!.toLocalDate()).`as`("soknadsbarnFodselsdato").isEqualTo(LocalDate.now()) }
    )
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal slette aktivt vedtak fordi resultatKode i VedtakHendelse er AVSLAG`() {

    whenever(aktivtVedtakServiceMock.finnAktivtVedtak(any())).thenReturn(lagFinnAktivtVedtakDto())
    doNothing().whenever(aktivtVedtakServiceMock).slettAktivtVedtak(MockitoHelper.capture(slettAktivtVedtakCaptor))

    // Oppretter ny hendelse
    val nyHendelse = VedtakHendelse(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      stonadType = StonadType.FORSKUDD,
      sakId = sakId,
      skyldnerId = "12345",
      kravhaverId = kravhaverId,
      mottakerId = mottakerId,
      opprettetAv = "TEST",
      opprettetTimestamp = dateTimeNow,
      periodeListe = listOf(
        VedtakHendelsePeriode(
          periodeFom = dateNow.minusYears(1).withDayOfMonth(1),
          periodeTil = dateNow.withDayOfMonth(1),
          belop = belop1,
          valutakode = valutakode1,
          resultatkode = resultatkode1
        ),
        VedtakHendelsePeriode(
          periodeFom = dateNow.withDayOfMonth(1),
          periodeTil = null,
          belop = belop2,
          valutakode = valutakode2,
          resultatkode = "AVSLAG"
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    // Henter nytt aktivt vedtak som ville vært input til aktivtVedtakService.slettAktivtVedtak(id)
    val aktivtVedtakSomSkalSlettes = slettAktivtVedtakCaptor.value

    verify(this.aktivtVedtakServiceMock, times(0)).opprettNyttAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).oppdaterAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(1)).slettAktivtVedtak(any())

    assertAll(
      Executable { assertThat(aktivtVedtakSomSkalSlettes).isNotNull() },
      Executable { assertThat(aktivtVedtakSomSkalSlettes).isEqualTo(vedtakId) }
    )
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal ikke gjøre noe fordi aktivt vedtak ikke finnes og resultatKode i VedtakHendelse er AVSLAG`() {

    whenever(aktivtVedtakServiceMock.finnAktivtVedtak(any())).thenReturn(null)

    // Oppretter ny hendelse
    val nyHendelse = VedtakHendelse(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      stonadType = StonadType.FORSKUDD,
      sakId = sakId,
      skyldnerId = "12345",
      kravhaverId = kravhaverId,
      mottakerId = mottakerId,
      opprettetAv = "TEST",
      opprettetTimestamp = dateTimeNow,
      periodeListe = listOf(
        VedtakHendelsePeriode(
          periodeFom = dateNow.minusYears(1).withDayOfMonth(1),
          periodeTil = dateNow.withDayOfMonth(1),
          belop = belop1,
          valutakode = valutakode1,
          resultatkode = resultatkode1
        ),
        VedtakHendelsePeriode(
          periodeFom = dateNow.withDayOfMonth(1),
          periodeTil = null,
          belop = belop2,
          valutakode = valutakode2,
          resultatkode = "AVSLAG"
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    verify(this.aktivtVedtakServiceMock, times(0)).opprettNyttAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).oppdaterAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).slettAktivtVedtak(any())
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal ikke gjøre noe fordi aktivt vedtak ikke finnes og vedtakType i VedtakHendelse er noe annet enn MANUELT`() {

    whenever(aktivtVedtakServiceMock.finnAktivtVedtak(any())).thenReturn(null)

    // Oppretter ny hendelse
    val nyHendelse = VedtakHendelse(
      vedtakId = vedtakId,
      vedtakType = VedtakType.INDEKSREGULERING,
      stonadType = StonadType.FORSKUDD,
      sakId = sakId,
      skyldnerId = "12345",
      kravhaverId = kravhaverId,
      mottakerId = mottakerId,
      opprettetAv = "TEST",
      opprettetTimestamp = dateTimeNow,
      periodeListe = listOf(
        VedtakHendelsePeriode(
          periodeFom = dateNow.minusYears(1).withDayOfMonth(1),
          periodeTil = dateNow.withDayOfMonth(1),
          belop = belop1,
          valutakode = valutakode1,
          resultatkode = resultatkode1
        ),
        VedtakHendelsePeriode(
          periodeFom = dateNow.withDayOfMonth(1),
          periodeTil = null,
          belop = belop2,
          valutakode = valutakode2,
          resultatkode = resultatkode2
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    verify(this.aktivtVedtakServiceMock, times(0)).opprettNyttAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).oppdaterAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).slettAktivtVedtak(any())
  }

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal ikke gjøre noe fordi stonadType i VedtakHendelse er noe annet enn FORSKUDD`() {

    // Oppretter ny hendelse
    val nyHendelse = VedtakHendelse(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      stonadType = StonadType.BIDRAG,
      sakId = sakId,
      skyldnerId = "12345",
      kravhaverId = kravhaverId,
      mottakerId = mottakerId,
      opprettetAv = "TEST",
      opprettetTimestamp = dateTimeNow,
      periodeListe = listOf(
        VedtakHendelsePeriode(
          periodeFom = dateNow.minusYears(1).withDayOfMonth(1),
          periodeTil = dateNow.withDayOfMonth(1),
          belop = belop1,
          valutakode = valutakode1,
          resultatkode = resultatkode1
        ),
        VedtakHendelsePeriode(
          periodeFom = dateNow.withDayOfMonth(1),
          periodeTil = null,
          belop = belop2,
          valutakode = valutakode2,
          resultatkode = resultatkode2
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    verify(this.aktivtVedtakServiceMock, times(0)).opprettNyttAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).oppdaterAktivtVedtak(any())
    verify(this.aktivtVedtakServiceMock, times(0)).slettAktivtVedtak(any())
  }

  private fun lagVedtakDtoNytt() =
    VedtakDto(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      opprettetAv = "",
      vedtakDato = dateNow,
      enhetId = "",
      opprettetTimestamp = dateTimeNow,
      listOf(
        GrunnlagDto(
          grunnlagId = 1,
          referanse = "Mottatt_Sivilstand",
          type = GrunnlagType.SIVILSTAND,
          innhold = """{
        "rolle": "${Rolle.BIDRAGSMOTTAKER}",
        "datoFom": "$dateNow",
        "datoTil": null,
        "sivilstandKode": "$sivilstandkode1"
      }"""
        ),
        GrunnlagDto(
          grunnlagId = 2,
          referanse = "Mottatt_Bostatus",
          type = GrunnlagType.BOSTATUS,
          innhold = """{
        "rolle": "${Rolle.SOKNADSBARN}",
        "datoFom": "$dateNow",
        "datoTil": null,
        "bostatusKode": "$bostatuskode1"
      }"""
        ),
        GrunnlagDto(
          grunnlagId = 3,
          referanse = "Mottatt_Barn",
          type = GrunnlagType.BARN,
          innhold = """{
        "rolle": "${Rolle.SOKNADSBARN}",
        "fodselsdato": "$fodselsdato"
      }"""
        ),
      ),
      listOf(
        StonadsendringDto(
          stonadType = StonadType.FORSKUDD,
          sakId = "SAK-001",
          behandlingId = "",
          skyldnerId = "12345",
          kravhaverId = "54321",
          mottakerId = "24680",
          listOf(
            VedtakPeriodeDto(
              periodeFomDato = LocalDate.now(),
              periodeTilDato = null,
              belop = BigDecimal.ZERO,
              valutakode = "NOK",
              resultatkode = "RESULTAT",
              grunnlagReferanseListe = listOf("Mottatt_Sivilstand", "Mottatt_Bostatus", "Mottatt_Barn")
            )
          )
        )
      ),
      emptyList(),
      emptyList()
    )

  private fun lagVedtakDtoOppdatert() =
    VedtakDto(
      vedtakId = vedtakId,
      vedtakType = vedtakType,
      opprettetAv = "",
      vedtakDato = dateNow,
      enhetId = "",
      opprettetTimestamp = dateTimeNow,
      listOf(
        GrunnlagDto(
          grunnlagId = 1,
          referanse = "Mottatt_Sivilstand",
          type = GrunnlagType.SIVILSTAND,
          innhold = """{
        "rolle": "${Rolle.BIDRAGSMOTTAKER}",
        "datoFom": "$dateNow",
        "datoTil": null,
        "sivilstandKode": "$sivilstandkode2"
      }"""
        ),
        GrunnlagDto(
          grunnlagId = 2,
          referanse = "Mottatt_Bostatus",
          type = GrunnlagType.BOSTATUS,
          innhold = """{
        "rolle": "${Rolle.SOKNADSBARN}",
        "datoFom": "$dateNow",
        "datoTil": null,
        "bostatusKode": "$bostatuskode2"
      }"""
        ),
        GrunnlagDto(
          grunnlagId = 3,
          referanse = "Mottatt_Barn",
          type = GrunnlagType.BARN,
          innhold = """{
        "rolle": "${Rolle.SOKNADSBARN}",
        "fodselsdato": "$fodselsdato"
      }"""
        ),
      ),
      listOf(
        StonadsendringDto(
          stonadType = StonadType.FORSKUDD,
          sakId = "SAK-001",
          behandlingId = "",
          skyldnerId = "12345",
          kravhaverId = "54321",
          mottakerId = "24680",
          listOf(
            VedtakPeriodeDto(
              periodeFomDato = LocalDate.now(),
              periodeTilDato = null,
              belop = BigDecimal.ZERO,
              valutakode = "NOK",
              resultatkode = "RESULTAT",
              grunnlagReferanseListe = listOf("Mottatt_Sivilstand", "Mottatt_Bostatus", "Mottatt_Barn")
            )
          )
        )
      ),
      emptyList(),
      emptyList()
    )

  private fun lagFinnAktivtVedtakDto() =
    FinnAktivtVedtakDto(
      aktivtVedtakId = aktivtVedtakId,
      vedtakId = vedtakId,
      sakId = sakId,
      soknadsbarnId = soknadsbarnId,
      mottakerId = mottakerId,
      vedtakDatoSisteVedtak = dateNow.minusYears(1),
      vedtakDatoSisteManuelleVedtak = dateNow.minusYears(1),
      vedtakType = vedtakType,
      belop = belop1,
      valutakode = valutakode1,
      resultatkode = resultatkode1,
      mottakerSivilstandSisteManuelleVedtak = sivilstandkode1,
      mottakerAntallBarnSisteManuelleVedtak = antallBarn,
      soknadsbarnBostedsstatus = bostatuskode1,
      soknadsbarnFodselsdato = LocalDate.parse(fodselsdato),
      soknadsbarnHarUnntakskode = false,
      opprettetTimestamp = dateTimeNow.minusYears(1),
      sistEndretTimestamp = null
    )

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

    val aktivtVedtakId = 1
    val soknadsbarnId = "54321"
    val antallBarn = 0

    val belop2 = BigDecimal.valueOf(200)
    val valutakode2 = "EUR"
    val resultatkode2 = "RESULTATKODE2"
    val sivilstandkode2 = SivilstandKode.SAMBOER
    val bostatuskode2 = BostatusKode.ALENE
  }

  object MockitoHelper {

    // use this in place of captor.capture() if you are trying to capture an argument that is not nullable
    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
  }
}
