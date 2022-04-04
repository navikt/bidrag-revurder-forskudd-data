package no.nav.bidrag.revurder.forskudd.data.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.bidrag.revurder.forskudd.data.dto.FinnAktivtVedtakDto
import no.nav.bidrag.revurder.forskudd.data.dto.NyttAktivtVedtakRequestDto
import no.nav.bidrag.revurder.forskudd.data.dto.toAktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.service.AktivtVedtakService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AktivtVedtakController(private val aktivtVedtakService: AktivtVedtakService) {

  @PostMapping(AKTIVT_VEDTAK_NY)
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Aktivt vedtak opprettet"),
      ApiResponse(responseCode = "400", description = "Feil opplysinger oppgitt", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "401", description = "Sikkerhetstoken mangler, er utløpt, eller av andre årsaker ugyldig", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "500", description = "Serverfeil", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "503", description = "Tjeneste utilgjengelig", content = [Content(schema = Schema(hidden = true))])
    ]
  )

  // Kun for testformål?
  fun opprettNyttAktivtVedtak(@RequestBody request: NyttAktivtVedtakRequestDto): ResponseEntity<Int> {
    val aktivtVedtakOpprettet = aktivtVedtakService.opprettNyttAktivtVedtak(request.toAktivtVedtakBo())
    LOGGER.info("Følgende aktivt vedtak er opprettet: $aktivtVedtakOpprettet")
    return ResponseEntity(aktivtVedtakOpprettet, HttpStatus.OK)
  }


  @GetMapping("$AKTIVT_VEDTAK_SOK/{aktivtVedtakId}")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Aktivt vedtak funnet"),
      ApiResponse(responseCode = "401", description = "Sikkerhetstoken mangler, er utløpt, eller av andre årsaker ugyldig", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "403", description = "Manglende tilgang til å lese data for aktivt vedtak", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "404", description = "Aktivt vedtak ikke funnet", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "500", description = "Serverfeil", content = [Content(schema = Schema(hidden = true))]),
      ApiResponse(responseCode = "503", description = "Tjeneste utilgjengelig", content = [Content(schema = Schema(hidden = true))])
    ]
  )

  fun finnAktivtVedtak(@PathVariable aktivtVedtakId: Int): ResponseEntity<FinnAktivtVedtakDto> {
    val aktivtVedtakFunnet = aktivtVedtakService.finnAktivtVedtakFraId(aktivtVedtakId)
    LOGGER.info("Følgende aktivt vedtak ble funnet: $aktivtVedtakFunnet")
    return ResponseEntity(aktivtVedtakFunnet, HttpStatus.OK)
  }

  companion object {
    const val AKTIVT_VEDTAK_NY = "/aktivtvedtak/ny"
    const val AKTIVT_VEDTAK_SOK = "/aktivtvedtak"
    private val LOGGER = LoggerFactory.getLogger(AktivtVedtakController::class.java)
  }
}
