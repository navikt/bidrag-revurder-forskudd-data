package no.nav.bidrag.revurder.forskudd.data.persistence.repository

import no.nav.bidrag.revurder.forskudd.data.persistence.entity.AktivtVedtak
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AktivtVedtakRepository : CrudRepository<AktivtVedtak, Int?>{

  @Query(
    "select av from AktivtVedtak av where av.soknadsbarnId = :soknadsbarnId")
  fun finnAktivtVedtak(soknadsbarnId: String): AktivtVedtak?

//  @Query(
//    "update AktivtVedtak av set st.endretAvSaksbehandlerId = :saksbehandlerId, st.endretTimestamp = CURRENT_TIMESTAMP where st.stonadId = :stonadId")
//  @Modifying
//  fun oppdaterStonadMedEndretAvSaksbehandlerIdOgTimestamp(stonadId: Int, saksbehandlerId: String)
//
//  @Query(
//    "update Stonad st set st.mottakerId = :mottakerId, st.endretAvSaksbehandlerId = :saksbehandlerId, st.endretTimestamp = CURRENT_TIMESTAMP where st.stonadId = :stonadId")
//  @Modifying
//  fun endreMottakerIdForStonad(stonadId: Int, mottakerId: String, saksbehandlerId: String)
}
