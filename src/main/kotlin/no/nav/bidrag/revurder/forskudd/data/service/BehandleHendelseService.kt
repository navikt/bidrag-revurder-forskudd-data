package no.nav.bidrag.revurder.forskudd.data.service

import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakClient
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakDto
import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.GrunnlagType
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.StonadType
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelsePeriode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

private val LOGGER = LoggerFactory.getLogger(BehandleHendelseService::class.java)

interface BehandleHendelseService {

  fun behandleHendelse(vedtakHendelse: VedtakHendelse)
}

@Service
class DefaultBehandleHendelseService(
  private val aktivtVedtakService: AktivtVedtakService,
  private val vedtakClient: VedtakClient
) : BehandleHendelseService {

  override fun behandleHendelse(vedtakHendelse: VedtakHendelse) {
    if (vedtakHendelse.stonadType == StonadType.FORSKUDD) {
      behandleAktivtVedtakHendelse(vedtakHendelse)
      LOGGER.info("Behandler vedtakHendelse: $vedtakHendelse")
    }
  }

  // Sjekker om det finnes et aktivt vedtak med samme soknadsbarnId (kravhaverId) som i den nye hendelsen. 4 mulige utfall:
  // - vedtakHendelse har resultatkode 'AVSLAG' i den siste perioden og det finnes et aktivt vedtak: Slett det aktive vedtaket
  // - vedtakHendelse har ikke resultatkode 'AVSLAG' i den siste perioden og det finnes et aktivt vedtak: Oppdater det aktive vedtaket
  // - vedtakHendelse har resultatkode 'AVSLAG' i den siste perioden og det finnes ikke et aktivt vedtak: Ikke gjør noenting
  // - vedtakHendelse har ikke resultatkode 'AVSLAG' i den siste perioden og det finnes ikke et aktivt vedtak: Opprett et nytt aktivt vedtak (men kun
  //   hvis vedtakType = 'MANUELT'
  private fun behandleAktivtVedtakHendelse(vedtakHendelse: VedtakHendelse) {

    LOGGER.info("Behandler ny hendelse med vedtakId: ${vedtakHendelse.vedtakId}")
    val vedtakHendelseSistePeriode = hentVedtakHendelseSistePeriode(vedtakHendelse)
    val eksisterendeAktivtVedtak = aktivtVedtakService.finnAktivtVedtak(vedtakHendelse.kravhaverId)
    if (eksisterendeAktivtVedtak != null) {
      if (vedtakHendelseSistePeriode.resultatkode == "AVSLAG") {
        LOGGER.info("Aktivt vedtak funnet (vil bli slettet fordi resultatkode på vedtakHendelse er AVSLAG): $eksisterendeAktivtVedtak")
        slettEksisterendeAktivtVedtak(eksisterendeAktivtVedtak.aktivtVedtakId)
      } else {
        LOGGER.info("Aktivt vedtak funnet (vil bli oppdatert): $eksisterendeAktivtVedtak")
        val bidragVedtakData = hentBidragVedtakData(vedtakHendelse.vedtakId)
        oppdaterEksisterendeAktivtVedtak(eksisterendeAktivtVedtak, vedtakHendelse, vedtakHendelseSistePeriode, bidragVedtakData)
      }
    } else {
      if ((vedtakHendelseSistePeriode.resultatkode != "AVSLAG") && (vedtakHendelse.vedtakType == VedtakType.MANUELT)) {
        LOGGER.info("Aktivt vedtak ikke funnet. Nytt aktivt vedtak vil bli opprettet.")
        val bidragVedtakData = hentBidragVedtakData(vedtakHendelse.vedtakId)
        opprettNyttAktivtVedtak(vedtakHendelse, vedtakHendelseSistePeriode, bidragVedtakData)
      } else {
        LOGGER.info("Aktivt vedtak ikke funnet, men resultatkode til hendelse er 'AVSLAG' eller vedtakType er 'MANUELT'. Nytt aktivt vedtak vil ikke bli opprettet.")
      }
    }
  }

  // Sjekker at det finnes minst en periode i periodelista tilhørende vedtaket. Den siste perioden bør ha periodeTil = null og det er den som er
  // interessant ifbm revurdering forskudd. Hvis det ikke finnes en periode som oppfyller kriteriet returneres en periode med default-verdier.
  private fun hentVedtakHendelseSistePeriode(vedtakHendelse: VedtakHendelse): VedtakHendelsePeriode {
    return if (vedtakHendelse.periodeListe.isNotEmpty() && vedtakHendelse.periodeListe.last().periodeTil == null) {
      vedtakHendelse.periodeListe.last()
    } else {
      //TODO Sjekk hva som skal returneres her. Bør f.eks. "fiktivt beløp" settes så høyt at det alltid vil bli satt ned i en revurdering eller bør
      //TODO det gis en feilmelding?
      VedtakHendelsePeriode(
        periodeFom = LocalDate.now(),
        periodeTil = null,
        belop = BigDecimal.ZERO,
        valutakode = "NOK",
        resultatkode = ""
      )
    }
  }

  // Henter data fra bidrag-vedtak. Dette er data som ikke er del av VedtakHendelse.
  private fun hentBidragVedtakData(vedtakId: Int): BidragVedtakData {
    //TODO Feilhåndtering
    return mapResponsTilInternStruktur(vedtakClient.hentVedtak(vedtakId))
  }

  private fun mapResponsTilInternStruktur(hentVedtakResponse: VedtakDto): BidragVedtakData {
    val stonadsendring = hentVedtakResponse.stonadsendringListe.first { it.stonadType == StonadType.FORSKUDD }
    val periode = stonadsendring.periodeListe.first { it.periodeTilDato == null }
    val grunnlagReferanseListe = periode.grunnlagReferanseListe
    val grunnlagSivilstand = hentVedtakResponse.grunnlagListe.first {
      it.type == GrunnlagType.SIVILSTAND &&
          grunnlagReferanseListe.contains(it.referanse)
    }
    val grunnlagBostatus = hentVedtakResponse.grunnlagListe.first {
      it.type == GrunnlagType.BOSTATUS &&
          grunnlagReferanseListe.contains(it.referanse)
    }
    val grunnlagBarn = hentVedtakResponse.grunnlagListe.first {
      it.type == GrunnlagType.BARN &&
          grunnlagReferanseListe.contains(it.referanse)
    }
    //TODO Feilhåndtering av Json-mappinger
    //TODO Sjekke at det er et manuelt vedtak
    val mottakerSivilstandSisteManuelleVedtak = grunnlagSivilstand.innhold.get("sivilstandKode")!!.asText()
    //TODO Sjekke at det er et manuelt vedtak + skrive om antall barn - logikken i bidrag-beregn-forskudd-rest
    //val mottakerAntallBarnSisteManuelleVedtak = 0
    val soknadsbarnBostedsstatus = grunnlagBostatus.innhold.get("bostatusKode")!!.asText()
    val soknadsbarnFodselsdato = grunnlagBarn.innhold.get("fodselsdato")!!.asText()
    //TODO Må implementeres i bidrag-vedtak
    //val soknadsbarnHarUnntakskode = false

    return BidragVedtakData(
      mottakerSivilstandSisteManuelleVedtak = SivilstandKode.valueOf(mottakerSivilstandSisteManuelleVedtak),
      soknadsbarnBostedsstatus = BostatusKode.valueOf(soknadsbarnBostedsstatus),
      soknadsbarnFodselsdato = LocalDate.parse(soknadsbarnFodselsdato)
    )
  }

  private fun slettEksisterendeAktivtVedtak(eksisterendeAktivtVedtakId: Int) {
    aktivtVedtakService.slettAktivtVedtak(eksisterendeAktivtVedtakId)
  }

  private fun oppdaterEksisterendeAktivtVedtak(
    eksisterendeAktivtVedtak: AktivtVedtakBo,
    vedtakHendelse: VedtakHendelse,
    vedtakHendelsePeriode: VedtakHendelsePeriode,
    bidragVedtakData: BidragVedtakData
  ) {

    val oppdatertAktivtVedtak = AktivtVedtakBo(
      aktivtVedtakId = eksisterendeAktivtVedtak.aktivtVedtakId,
      vedtakId = vedtakHendelse.vedtakId,
      sakId = vedtakHendelse.sakId,
      soknadsbarnId = eksisterendeAktivtVedtak.soknadsbarnId,
      mottakerId = vedtakHendelse.mottakerId,
      vedtakDatoSisteVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(),
      vedtakDatoSisteManuelleVedtak = if (vedtakHendelse.vedtakType == VedtakType.MANUELT) vedtakHendelse.opprettetTimestamp.toLocalDate() else eksisterendeAktivtVedtak.vedtakDatoSisteManuelleVedtak,
      vedtakType = vedtakHendelse.vedtakType,
      belop = vedtakHendelsePeriode.belop,
      valutakode = vedtakHendelsePeriode.valutakode,
      resultatkode = vedtakHendelsePeriode.resultatkode,
      mottakerSivilstandSisteManuelleVedtak = if (vedtakHendelse.vedtakType == VedtakType.MANUELT) bidragVedtakData.mottakerSivilstandSisteManuelleVedtak else eksisterendeAktivtVedtak.mottakerSivilstandSisteManuelleVedtak,
      mottakerAntallBarnSisteManuelleVedtak = if (vedtakHendelse.vedtakType == VedtakType.MANUELT) bidragVedtakData.mottakerAntallBarnSisteManuelleVedtak else eksisterendeAktivtVedtak.mottakerAntallBarnSisteManuelleVedtak,
      soknadsbarnBostedsstatus = bidragVedtakData.soknadsbarnBostedsstatus,
      soknadsbarnFodselsdato = eksisterendeAktivtVedtak.soknadsbarnFodselsdato,
      soknadsbarnHarUnntakskode = bidragVedtakData.soknadsbarnHarUnntakskode,
      opprettetTimestamp = eksisterendeAktivtVedtak.opprettetTimestamp,
      sistEndretTimestamp = LocalDateTime.now()
    )

    aktivtVedtakService.oppdaterAktivtVedtak(oppdatertAktivtVedtak)
  }

  private fun opprettNyttAktivtVedtak(
    vedtakHendelse: VedtakHendelse,
    vedtakHendelsePeriode: VedtakHendelsePeriode,
    bidragVedtakData: BidragVedtakData
  ) {

    val nyttAktivtVedtak = AktivtVedtakBo(
      aktivtVedtakId = 0,
      vedtakId = vedtakHendelse.vedtakId,
      sakId = vedtakHendelse.sakId,
      soknadsbarnId = vedtakHendelse.kravhaverId,
      mottakerId = vedtakHendelse.mottakerId,
      vedtakDatoSisteVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(),
      vedtakDatoSisteManuelleVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(),
      vedtakType = vedtakHendelse.vedtakType,
      belop = vedtakHendelsePeriode.belop,
      valutakode = vedtakHendelsePeriode.valutakode,
      resultatkode = vedtakHendelsePeriode.resultatkode,
      mottakerSivilstandSisteManuelleVedtak = bidragVedtakData.mottakerSivilstandSisteManuelleVedtak,
      mottakerAntallBarnSisteManuelleVedtak = bidragVedtakData.mottakerAntallBarnSisteManuelleVedtak,
      soknadsbarnBostedsstatus = bidragVedtakData.soknadsbarnBostedsstatus,
      soknadsbarnFodselsdato = bidragVedtakData.soknadsbarnFodselsdato,
      soknadsbarnHarUnntakskode = bidragVedtakData.soknadsbarnHarUnntakskode,
      opprettetTimestamp = LocalDateTime.now(),
      sistEndretTimestamp = null
    )

    aktivtVedtakService.opprettNyttAktivtVedtak(nyttAktivtVedtak)
  }
}

data class BidragVedtakData(
  val mottakerSivilstandSisteManuelleVedtak: SivilstandKode,
  val mottakerAntallBarnSisteManuelleVedtak: Int = 0,
  val soknadsbarnBostedsstatus: BostatusKode,
  val soknadsbarnFodselsdato: LocalDate,
  val soknadsbarnHarUnntakskode: Boolean = false
)
