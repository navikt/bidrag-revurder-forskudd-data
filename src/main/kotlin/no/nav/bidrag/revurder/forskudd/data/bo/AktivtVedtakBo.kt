package no.nav.bidrag.revurder.forskudd.data.bo

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.persistence.entity.AktivtVedtak
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties

data class AktivtVedtakBo(

    @Schema(description = "Aktivt vedtak id")
    val aktivtVedtakId: Int,

    @Schema(description = "Vedtak id")
    val vedtakId: Int,

    @Schema(description = "Sak id")
    val sakId: String?,

    @Schema(description = "Søknadsbarn id")
    val soknadsbarnId: String,

    @Schema(description = "Mottaker id")
    val mottakerId: String,

    @Schema(description = "Dato for siste vedtak")
    val vedtakDatoSisteVedtak: LocalDate,

    @Schema(description = "Dato for siste manuelle vedtak")
    val vedtakDatoSisteManuelleVedtak: LocalDate,

    @Schema(description = "Vedtaktype")
    val vedtakType: VedtakType,

    @Schema(description = "Beløp")
    val belop: BigDecimal,

    @Schema(description = "Valutakode")
    val valutakode: String,

    @Schema(description = "Resultatkode")
    val resultatkode: String,

    @Schema(description = "Mottakers sivilstand siste manuelle vedtak")
    val mottakerSivilstandSisteManuelleVedtak: SivilstandKode,

    @Schema(description = "Mottakers antall barn siste manuelle vedtak")
    val mottakerAntallBarnSisteManuelleVedtak: Int,

    @Schema(description = "Søknadsbarnets bostedsstatus")
    val soknadsbarnBostedsstatus: BostatusKode,

    @Schema(description = "Søknadsbarnets fødselsdato")
    val soknadsbarnFodselsdato: LocalDate,

    @Schema(description = "Søknadsbarnet har unntakskode")
    val soknadsbarnHarUnntakskode: Boolean,

    @Schema(description = "Timestamp for opprettelse av forekomst")
    val opprettetTimestamp: LocalDateTime,

    @Schema(description = "Timestamp for siste endring av forekomst")
    val sistEndretTimestamp: LocalDateTime?
)

fun AktivtVedtakBo.toAktivtVedtakEntity() = with(::AktivtVedtak) {
    val propertiesByName = AktivtVedtakBo::class.memberProperties.associateBy { it.name }
    callBy(
        parameters.associateWith { parameter ->
            when (parameter.name) {
                AktivtVedtak::vedtakType.name -> vedtakType.toString()
                AktivtVedtak::mottakerSivilstandSisteManuelleVedtak.name -> mottakerSivilstandSisteManuelleVedtak.toString()
                AktivtVedtak::soknadsbarnBostedsstatus.name -> soknadsbarnBostedsstatus.toString()
                else -> propertiesByName[parameter.name]?.get(this@toAktivtVedtakEntity)
            }
        }
    )
}
