package no.nav.bidrag.revurder.forskudd.data.persistence.entity

import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import kotlin.reflect.full.memberProperties

@Entity
data class AktivtVedtak(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, name = "aktivt_vedtak_id")
  val aktivtVedtakId: Int,

  @Column(nullable = false, name = "vedtak_id")
  val vedtakId: Int,

  @Column(nullable = true, name = "sak_id")
  val sakId: String?,

  @Column(nullable = false, name = "soknadsbarn_id")
  val soknadsbarnId: String,

  @Column(nullable = false, name = "mottaker_id")
  val mottakerId: String,

  @Column(nullable = false, name = "vedtak_dato_siste_vedtak")
  val vedtakDatoSisteVedtak: LocalDate,

  @Column(nullable = false, name = "vedtak_dato_siste_manuelle_vedtak")
  val vedtakDatoSisteManuelleVedtak: LocalDate,

  @Column(nullable = false, name = "vedtak_type")
  val vedtakType: String,

  @Column(nullable = false, name = "belop")
  val belop: BigDecimal,

  @Column(nullable = false, name = "valutakode")
  val valutakode: String,

  @Column(nullable = false, name = "resultatkode")
  val resultatkode: String,

  @Column(nullable = false, name = "mottaker_sivilstand_siste_manuelle_vedtak")
  val mottakerSivilstandSisteManuelleVedtak: String,

  @Column(nullable = false, name = "mottaker_antall_barn_siste_manuelle_vedtak")
  val mottakerAntallBarnSisteManuelleVedtak: Int,

  @Column(nullable = false, name = "soknadsbarn_bostedsstatus")
  val soknadsbarnBostedsstatus: String,

  @Column(nullable = false, name = "soknadsbarn_fodselsdato")
  val soknadsbarnFodselsdato: LocalDate,

  @Column(nullable = false, name = "soknadsbarn_har_unntakskode")
  val soknadsbarnHarUnntakskode: Boolean,

  @Column(nullable = false, name = "opprettet_timestamp")
  val opprettetTimestamp: LocalDateTime,

  @Column(nullable = true, name = "sist_endret_timestamp")
  val sistEndretTimestamp: LocalDateTime?
)

fun AktivtVedtak.toAktivtVedtakBo() = with(::AktivtVedtakBo) {
  val propertiesByName = AktivtVedtak::class.memberProperties.associateBy { it.name }
  callBy(parameters.associateWith { parameter ->
    when (parameter.name) {
      AktivtVedtakBo::vedtakType.name -> VedtakType.valueOf(vedtakType)
      AktivtVedtakBo::mottakerSivilstandSisteManuelleVedtak.name -> SivilstandKode.valueOf(mottakerSivilstandSisteManuelleVedtak)
      AktivtVedtakBo::soknadsbarnBostedsstatus.name -> BostatusKode.valueOf(soknadsbarnBostedsstatus)
      else -> propertiesByName[parameter.name]?.get(this@toAktivtVedtakBo)
    }
  })
}
