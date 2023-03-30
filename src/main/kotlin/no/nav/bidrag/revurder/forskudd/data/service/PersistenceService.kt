package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.bo.toAktivtVedtakEntity
import no.nav.bidrag.revurder.forskudd.data.persistence.entity.toAktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.persistence.repository.AktivtVedtakRepository
import org.springframework.stereotype.Service

@Service
class PersistenceService(val aktivtVedtakRepository: AktivtVedtakRepository) {

    fun opprettNyttAktivtVedtak(aktivtVedtakBo: AktivtVedtakBo): AktivtVedtakBo {
        val nyttAktivtVedtak = aktivtVedtakBo.toAktivtVedtakEntity()
        val aktivtVedtak = aktivtVedtakRepository.save(nyttAktivtVedtak)
        return aktivtVedtak.toAktivtVedtakBo()
    }

    fun oppdaterAktivtVedtak(aktivtVedtakBo: AktivtVedtakBo): AktivtVedtakBo {
        val oppdatertAktivtVedtak = aktivtVedtakBo.toAktivtVedtakEntity()
        val aktivtVedtak = aktivtVedtakRepository.save(oppdatertAktivtVedtak)
        return aktivtVedtak.toAktivtVedtakBo()
    }

    fun slettAktivtVedtak(aktivtVedtakId: Int) {
        aktivtVedtakRepository.deleteById(aktivtVedtakId)
    }

    fun finnAktivtVedtakFraId(aktivtVedtakId: Int): AktivtVedtakBo? {
        val aktivtVedtak = aktivtVedtakRepository.findById(aktivtVedtakId)
            .orElseThrow {
                IllegalArgumentException(String.format("Fant ikke aktivt vedtak med id %d i databasen", aktivtVedtakId))
            }
        return aktivtVedtak.toAktivtVedtakBo()
    }

    fun finnAktivtVedtak(soknadsbarnId: String): AktivtVedtakBo? {
        val aktivtVedtak = aktivtVedtakRepository.finnAktivtVedtak(soknadsbarnId)
        return aktivtVedtak?.toAktivtVedtakBo()
    }
}
