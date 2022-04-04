package no.nav.bidrag.revurder.forskudd.data.consumer.api

import io.swagger.v3.oas.annotations.media.Schema

@Schema
data class HentBehandlingsreferanseResponse(

  @Schema(description ="Kildesystem for behandlingen før vedtaket")
  val kilde: String = "",

  @Schema(description = "Kildesystemets referanse til behandlingen")
  val referanse: String = ""
)
