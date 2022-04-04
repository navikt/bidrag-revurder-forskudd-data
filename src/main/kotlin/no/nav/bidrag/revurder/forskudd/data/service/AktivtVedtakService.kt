package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.bo.toFinnAktivtVedtakDto
import no.nav.bidrag.revurder.forskudd.data.dto.FinnAktivtVedtakDto
import no.nav.bidrag.revurder.forskudd.data.dto.NyttAktivtVedtakRequestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AktivtVedtakService(val persistenceService: PersistenceService) {

  fun opprettNyttAktivtVedtak(aktivtVedtakBo: AktivtVedtakBo): Int {
    val opprettetAktivtVedtak = persistenceService.opprettNyttAktivtVedtak(aktivtVedtakBo)
    return opprettetAktivtVedtak.aktivtVedtakId
  }

  fun finnAktivtVedtakFraId(aktivtVedtakId: Int): FinnAktivtVedtakDto? {
    val aktivtVedtakDto = persistenceService.finnAktivtVedtakFraId(aktivtVedtakId)
    return aktivtVedtakDto?.toFinnAktivtVedtakDto()
  }

  // Sjekker om det allerede finnes et aktivt vedtak. soknadsbarnId (=kravhaverId fra vedtak-hendelse) vil være unikt, siden skyldnerId for
  // forskudd alltid vil være NAV
  fun finnAktivtVedtak(soknadsbarnId: String): FinnAktivtVedtakDto? {
    val aktivtVedtakDto = persistenceService.finnAktivtVedtak(soknadsbarnId)
    return aktivtVedtakDto?.toFinnAktivtVedtakDto()
  }

  fun oppdaterAktivtVedtak(eksisterendeAktivtVedtak: FinnAktivtVedtakDto, oppdatertAktivtVedtak: NyttAktivtVedtakRequestDto) {

    //TODO
  }

  fun slettAktivtVedtak(eksisterendeAktivtVedtak: FinnAktivtVedtakDto) {
    persistenceService.slettAktivtVedtak(eksisterendeAktivtVedtak.aktivtVedtakId)
  }
}
