package no.nav.bidrag.revurder.forskudd.data.consumer.api

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.behandling.felles.enums.VedtakType
import java.time.LocalDate
import java.time.LocalDateTime

@Schema
data class HentVedtakResponse(

  @Schema(description = "Vedtak-id")
  var vedtakId: Int,

  @Schema(description = "Vedtak-type")
  var vedtakType: VedtakType,

  @Schema(description = "Id til saksbehandler/batchjobb evt annet som opprettet vedtaket")
  var opprettetAv: String,

  @Schema(description = "Dato vedtaket er fattet")
  val vedtakDato: LocalDate? = null,

  @Schema(description = "Id til enheten som er ansvarlig for vedtaket")
  var enhetId: String,

  @Schema(description = "Opprettet timestamp")
  var opprettetTimestamp: LocalDateTime,

  @Schema(description = "Liste over alle grunnlag som inngår i vedtaket")
  var grunnlagListe: List<HentGrunnlagResponse> = emptyList(),

  @Schema(description = "Liste over alle stønadsendringer som inngår i vedtaket")
  var stonadsendringListe: List<HentStonadsendringResponse> = emptyList(),

  @Schema(description = "Liste over alle engangsbeløp som inngår i vedtaket")
  var engangsbelopListe: List<HentEngangsbelopResponse> = emptyList(),

  @Schema(description = "Liste med referanser til alle behandlinger som ligger som grunnlag til vedtaket")
  val behandlingsreferanseListe: List<HentBehandlingsreferanseResponse> = emptyList()
)