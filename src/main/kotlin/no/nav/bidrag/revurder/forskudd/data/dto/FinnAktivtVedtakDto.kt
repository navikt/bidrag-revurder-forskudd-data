package no.nav.bidrag.revurder.forskudd.data.dto

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.VedtakType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class FinnAktivtVedtakDto(

  @Schema(description = "Aktivt vedtak id")
  val aktivtVedtakId: Int = 0,

  @Schema(description = "Vedtak id")
  val vedtakId: Int = 0,

  @Schema(description = "Sak id")
  val sakId: String? = "",

  @Schema(description = "Søknadsbarn id")
  val soknadsbarnId: String = "",

  @Schema(description = "Mottaker id")
  val mottakerId: String = "",

  @Schema(description = "Dato for siste vedtak")
  val vedtakDatoSisteVedtak: LocalDate = LocalDate.now(),

  @Schema(description = "Dato for siste manuelle vedtak")
  val vedtakDatoSisteManuelleVedtak: LocalDate = LocalDate.now(),

  @Schema(description = "Vedtaktype")
  val vedtakType: VedtakType = VedtakType.MANUELT,

  @Schema(description = "Beløp")
  val belop: BigDecimal = BigDecimal.ZERO,

  @Schema(description = "Valutakode")
  val valutakode: String = "",

  @Schema(description = "Resultatkode")
  val resultatkode: String = "",

  @Schema(description = "Mottakers sivilstand siste manuelle vedtak")
  val mottakerSivilstandSisteManuelleVedtak: SivilstandKode = SivilstandKode.GIFT,

  @Schema(description = "Mottakers antall barn siste manuelle vedtak")
  val mottakerAntallBarnSisteManuelleVedtak: Int = 0,

  @Schema(description = "Søknadsbarnets bostedsstatus")
  val soknadsbarnBostedsstatus: BostatusKode = BostatusKode.MED_FORELDRE,

  @Schema(description = "Søknadsbarnets fødselsdato")
  val soknadsbarnFodselsdato: LocalDate = LocalDate.now(),

  @Schema(description = "Søknadsbarnet har unntakskode")
  val soknadsbarnHarUnntakskode: Boolean = false,

  @Schema(description = "Timestamp for opprettelse av forekomst")
  val opprettetTimestamp: LocalDateTime = LocalDateTime.now(),

  @Schema(description = "Timestamp for siste endring av forekomst")
  val sistEndretTimestamp: LocalDateTime? = LocalDateTime.now()
)
