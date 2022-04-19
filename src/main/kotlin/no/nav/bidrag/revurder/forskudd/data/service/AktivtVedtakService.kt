package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AktivtVedtakService(val persistenceService: PersistenceService) {

  fun opprettNyttAktivtVedtak(aktivtVedtakBo: AktivtVedtakBo): Int {
    val opprettetAktivtVedtak = persistenceService.opprettNyttAktivtVedtak(aktivtVedtakBo)
    return opprettetAktivtVedtak.aktivtVedtakId
  }

  fun oppdaterAktivtVedtak(aktivtVedtakBo: AktivtVedtakBo): Int {
    val oppdatertAktivtVedtak = persistenceService.oppdaterAktivtVedtak(aktivtVedtakBo)
    return oppdatertAktivtVedtak.aktivtVedtakId
  }

  fun slettAktivtVedtak(eksisterendeAktivtVedtakId: Int) {
    persistenceService.slettAktivtVedtak(eksisterendeAktivtVedtakId)
  }

  fun finnAktivtVedtakFraId(aktivtVedtakId: Int): AktivtVedtakBo? {
    return persistenceService.finnAktivtVedtakFraId(aktivtVedtakId)
  }

  // Sjekker om det allerede finnes et aktivt vedtak. soknadsbarnId (=kravhaverId fra vedtak-hendelse) vil være unikt, siden skyldnerId for
  // forskudd alltid vil være NAV
  fun finnAktivtVedtak(soknadsbarnId: String): AktivtVedtakBo? {
    return persistenceService.finnAktivtVedtak(soknadsbarnId)
  }
}
