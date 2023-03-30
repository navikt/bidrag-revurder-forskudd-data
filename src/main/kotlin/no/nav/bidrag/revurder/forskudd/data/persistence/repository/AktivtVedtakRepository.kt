package no.nav.bidrag.revurder.forskudd.data.persistence.repository

import no.nav.bidrag.revurder.forskudd.data.persistence.entity.AktivtVedtak
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AktivtVedtakRepository : CrudRepository<AktivtVedtak, Int?> {

    @Query(
        "select av from AktivtVedtak av where av.soknadsbarnId = :soknadsbarnId"
    )
    fun finnAktivtVedtak(soknadsbarnId: String): AktivtVedtak?
}
