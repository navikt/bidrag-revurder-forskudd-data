package no.nav.bidrag.revurder.forskudd.data.dto

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.reflect.full.memberProperties

@Schema(description = "Egenskaper ved et aktivt vedtak")
data class NyttAktivtVedtakRequestDto(

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
  val soknadsbarnHarUnntakskode: Boolean = false
)

fun NyttAktivtVedtakRequestDto.toAktivtVedtakBo() = with(::AktivtVedtakBo) {
  val propertiesByName = NyttAktivtVedtakRequestDto::class.memberProperties.associateBy { it.name }
  callBy(parameters.associateWith { parameter ->
    when (parameter.name) {
      AktivtVedtakBo::aktivtVedtakId.name -> 0
      else -> propertiesByName[parameter.name]?.get(this@toAktivtVedtakBo)
    }
  })
}
