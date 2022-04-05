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
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelsePeriode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Disabled
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
  private lateinit var nyttAktivtVedtakCaptor: ArgumentCaptor<AktivtVedtakBo>

  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal opprette nytt aktivt vedtak basert på data fra VedtakHendelse`() {

    whenever(aktivtVedtakServiceMock.finnAktivtVedtak(any())).thenReturn(null)
    whenever(aktivtVedtakServiceMock.opprettNyttAktivtVedtak(MockitoHelper.capture(nyttAktivtVedtakCaptor))).thenReturn(1)

    // Simulerer vedtak returnert fra bidrag-vedtak
    whenever(vedtakClientMock.hentVedtak(any())).thenReturn(lagVedtakDto())

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
          periodeFom = dateNow,
          periodeTil = null,
          belop = belop,
          valutakode = valutakode,
          resultatkode = resultatkode
        )
      )
    )

    // Kaller BehandleHendelseService
    behandleHendelseService.behandleHendelse(nyHendelse)

    // Henter nytt aktivt vedtak som ville vært input til aktivtVedtakService.opprettNyttAktivtVedtak(nyttAktivtVedtak)
    val nyttAktivtVedtakSomSkalOpprettes = nyttAktivtVedtakCaptor.value

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
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.belop).`as`("belop").isEqualTo(belop) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.valutakode).`as`("valutakode").isEqualTo(valutakode) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.resultatkode).`as`("resultatkode").isEqualTo(resultatkode) },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.mottakerSivilstandSisteManuelleVedtak).`as`("mottakerSivilstandSisteManuelleVedtak")
          .isEqualTo(sivilstandkode)
      },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.mottakerAntallBarnSisteManuelleVedtak).`as`("mottakerAntallBarnSisteManuelleVedtak")
          .isEqualTo(BidragVedtakData().mottakerAntallBarnSisteManuelleVedtak)
      },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnBostedsstatus).`as`("soknadsbarnBostedsstatus").isEqualTo(bostatuskode) },
      Executable { assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnFodselsdato).`as`("soknadsbarnFodselsdato").isEqualTo(fodselsdato) },
      Executable {
        assertThat(nyttAktivtVedtakSomSkalOpprettes.soknadsbarnHarUnntakskode).`as`("soknadsbarnHarUnntakskode")
          .isEqualTo(BidragVedtakData().soknadsbarnHarUnntakskode)
      }
    )
  }

  @Disabled
  @Test
  @Suppress("NonAsciiCharacters")
  fun `skal oppdatere aktivt vedtak basert på data fra VedtakHendelse`() {

  }

  fun lagVedtakDto() =
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
        "sivilstandKode": "$sivilstandkode"
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
        "bostatusKode": "$bostatuskode"
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

  companion object {

    val vedtakId = 1
    val vedtakType = VedtakType.MANUELT
    val sakId = "SAK-001"
    val kravhaverId = "54321"
    val mottakerId = "24680"
    val belop = BigDecimal.valueOf(100)
    val valutakode = "NOK"
    val resultatkode = "RESULTATKODE"
    val dateNow = LocalDate.now()
    val dateTimeNow = LocalDateTime.now()
    val sivilstandkode = SivilstandKode.GIFT
    val bostatuskode = BostatusKode.MED_FORELDRE
    val fodselsdato = "2006-02-01"
  }

  object MockitoHelper {

    // use this in place of captor.capture() if you are trying to capture an argument that is not nullable
    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
  }
}
