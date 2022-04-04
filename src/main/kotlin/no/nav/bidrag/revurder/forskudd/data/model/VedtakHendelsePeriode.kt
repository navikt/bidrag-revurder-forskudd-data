package no.nav.bidrag.revurder.forskudd.data.model

import java.math.BigDecimal
import java.time.LocalDate

data class VedtakHendelsePeriode(
  val periodeFom: LocalDate,
  val periodeTil: LocalDate?,
  val belop: BigDecimal,
  val valutakode: String,
  val resultatkode: String
)
