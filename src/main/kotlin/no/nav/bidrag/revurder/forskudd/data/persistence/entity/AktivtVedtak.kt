package no.nav.bidrag.revurder.forskudd.data.persistence.entity

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
  val aktivtVedtakId: Int = 0,

  @Column(nullable = false, name = "vedtak_id")
  val vedtakId: Int = 0,

  @Column(name = "sak_id")
  val sakId: String? = "",

  @Column(nullable = false, name = "soknadsbarn_id")
  val soknadsbarnId: String = "",

  @Column(nullable = false, name = "mottaker_id")
  val mottakerId: String = "",

  @Column(nullable = false, name = "vedtak_dato_siste_vedtak")
  val vedtakDatoSisteVedtak: LocalDate = LocalDate.now(),

  @Column(nullable = false, name = "vedtak_dato_siste_manuelle_vedtak")
  val vedtakDatoSisteManuelleVedtak: LocalDate = LocalDate.now(),

  @Column(nullable = false, name = "vedtak_type")
  val vedtakType: String = "",

  @Column(nullable = false, name = "belop")
  val belop: BigDecimal = BigDecimal.ZERO,

  @Column(nullable = false, name = "valutakode")
  val valutakode: String = "",

  @Column(nullable = false, name = "resultatkode")
  val resultatkode: String = "",

  @Column(nullable = false, name = "mottaker_sivilstand_siste_manuelle_vedtak")
  val mottakerSivilstandSisteManuelleVedtak: String = "",

  @Column(nullable = false, name = "mottaker_antall_barn_siste_manuelle_vedtak")
  val mottakerAntallBarnSisteManuelleVedtak: Int = 0,

  @Column(nullable = false, name = "soknadsbarn_bostedsstatus")
  val soknadsbarnBostedsstatus: String = "",

  @Column(nullable = false, name = "soknadsbarn_fodselsdato")
  val soknadsbarnFodselsdato: LocalDate = LocalDate.now(),

  @Column(nullable = false, name = "soknadsbarn_har_unntakskode")
  val soknadsbarnHarUnntakskode: Boolean = false,

  @Column(nullable = false, name = "opprettet_timestamp")
  val opprettetTimestamp: LocalDateTime = LocalDateTime.now(),

  @Column(nullable = false, name = "sist_endret_timestamp")
  val sistEndretTimestamp: LocalDateTime? = LocalDateTime.now()
)

fun AktivtVedtak.toAktivtVedtakBo() = with(::AktivtVedtakBo) {
  val propertiesByName = AktivtVedtak::class.memberProperties.associateBy { it.name }
  callBy(parameters.associateWith { parameter ->
    propertiesByName[parameter.name]?.get(this@toAktivtVedtakBo)
  })
}
