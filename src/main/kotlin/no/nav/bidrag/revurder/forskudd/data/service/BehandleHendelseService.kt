package no.nav.bidrag.revurder.forskudd.data.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakClient
import no.nav.bidrag.behandling.felles.dto.vedtak.VedtakDto
import no.nav.bidrag.behandling.felles.enums.GrunnlagType
import no.nav.bidrag.behandling.felles.enums.StonadType
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.dto.FinnAktivtVedtakDto
import no.nav.bidrag.revurder.forskudd.data.dto.NyttAktivtVedtakRequestDto
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelse
import no.nav.bidrag.revurder.forskudd.data.model.VedtakHendelsePeriode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

private val LOGGER = LoggerFactory.getLogger(DefaultBehandleHendelseService::class.java)

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

  private fun behandleAktivtVedtakHendelse(vedtakHendelse: VedtakHendelse) {
    val vedtakHendelseSistePeriode = hentVedtakHendelseSistePeriode(vedtakHendelse)
    val eksisterendeAktivtVedtak = aktivtVedtakService.finnAktivtVedtak(vedtakHendelse.kravhaverId)
    if (eksisterendeAktivtVedtak != null) {
      if (vedtakHendelseSistePeriode.resultatkode == "AVSLAG") {
        LOGGER.info("Aktivt vedtak funnet (vil bli slettet fordi resultatkode på vedtakHendelse er AVSLAG): $eksisterendeAktivtVedtak")
        slettEksisterendeAktivtVedtak(eksisterendeAktivtVedtak)
      } else {
        LOGGER.info("Aktivt vedtak funnet (vil bli oppdatert): $eksisterendeAktivtVedtak")
        val bidragVedtakData = hentBidragVedtakData(vedtakHendelse.vedtakId)
        oppdaterEksisterendeAktivtVedtak(eksisterendeAktivtVedtak, vedtakHendelse, vedtakHendelseSistePeriode, bidragVedtakData)
      }
    } else {
      LOGGER.info("Aktivt vedtak ikke funnet. Nytt aktivt vedtak vil bli opprettet.")
      val bidragVedtakData = hentBidragVedtakData(vedtakHendelse.vedtakId)
      //TODO Skal bare gjøres hvis vedtakType er MANUELL
      opprettNyttAktivtVedtak(vedtakHendelse, vedtakHendelseSistePeriode, bidragVedtakData)
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
    val grunnlagSivilstandInnholdJson = ObjectMapper().readTree(grunnlagSivilstand.innhold)
    val grunnlagBostatus = hentVedtakResponse.grunnlagListe.first {
      it.type == GrunnlagType.BOSTATUS &&
          grunnlagReferanseListe.contains(it.referanse)
    }
    val grunnlagBostatusInnholdJson = ObjectMapper().readTree(grunnlagBostatus.innhold)
    val grunnlagBarn = hentVedtakResponse.grunnlagListe.first {
      it.type == GrunnlagType.BARN &&
          grunnlagReferanseListe.contains(it.referanse)
    }
    val grunnlagBarnInnholdJson = ObjectMapper().readTree(grunnlagBarn.innhold)
    //TODO Feilhåndtering av Json-mappinger
    //TODO Sjekke at det er et manuelt vedtak
    val mottakerSivilstandSisteManuelleVedtak = grunnlagSivilstandInnholdJson.get("sivilstandKode")!!.asText()
    //TODO Sjekke at det er et manuelt vedtak + skrive om antall barn - logikken i bidrag-beregn-forskudd-rest
    //val mottakerAntallBarnSisteManuelleVedtak = 0
    val soknadsbarnBostedsstatus = grunnlagBostatusInnholdJson.get("bostatusKode")!!.asText()
    val soknadsbarnFodselsdato = grunnlagBarnInnholdJson.get("fodselsdato")!!.asText()
    //TODO Må implementeres i bidrag-vedtak
    //val soknadsbarnHarUnntakskode = false

    return BidragVedtakData(
      mottakerSivilstandSisteManuelleVedtak = mottakerSivilstandSisteManuelleVedtak,
      soknadsbarnBostedsstatus = soknadsbarnBostedsstatus,
      soknadsbarnFodselsdato = LocalDate.parse(soknadsbarnFodselsdato)
    )
  }

  private fun slettEksisterendeAktivtVedtak(eksisterendeAktivtVedtak: FinnAktivtVedtakDto) {
    aktivtVedtakService.slettAktivtVedtak(eksisterendeAktivtVedtak)
  }

  private fun oppdaterEksisterendeAktivtVedtak(
    eksisterendeAktivtVedtak: FinnAktivtVedtakDto,
    vedtakHendelse: VedtakHendelse,
    vedtakHendelsePeriode: VedtakHendelsePeriode,
    bidragVedtakData: BidragVedtakData
  ) {

    // Verifiser at følgende felter er like i eksisterende og nytt vedtak:
    if (eksisterendeAktivtVedtak.sakId.equals(vedtakHendelse.sakId) &&
      eksisterendeAktivtVedtak.mottakerId == vedtakHendelse.mottakerId
    ) {
      // Gjør ingenting
    } else {
      //TODO Feilhåndtering
    }

    val oppdatertAktivtVedtak = NyttAktivtVedtakRequestDto(
      vedtakId = vedtakHendelse.vedtakId,
      sakId = eksisterendeAktivtVedtak.sakId,
      soknadsbarnId = eksisterendeAktivtVedtak.soknadsbarnId,
      mottakerId = eksisterendeAktivtVedtak.mottakerId,
      vedtakDatoSisteVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(),
      vedtakDatoSisteManuelleVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(), //TODO Skal erstattes kun hvis vedtakHendelse.vedtakType = MANUELL
      vedtakType = vedtakHendelse.vedtakType.toString(),
      belop = vedtakHendelsePeriode.belop,
      valutakode = vedtakHendelsePeriode.valutakode,
      resultatkode = vedtakHendelsePeriode.resultatkode,
      mottakerSivilstandSisteManuelleVedtak = bidragVedtakData.mottakerSivilstandSisteManuelleVedtak, //TODO Skal erstattes kun hvis vedtakHendelse.vedtakType = MANUELL
      mottakerAntallBarnSisteManuelleVedtak = bidragVedtakData.mottakerAntallBarnSisteManuelleVedtak, //TODO Skal erstattes kun hvis vedtakHendelse.vedtakType = MANUELL
      soknadsbarnBostedsstatus = bidragVedtakData.soknadsbarnBostedsstatus,
      soknadsbarnFodselsdato = eksisterendeAktivtVedtak.soknadsbarnFodselsdato,
      soknadsbarnHarUnntakskode = bidragVedtakData.soknadsbarnHarUnntakskode
    )

    aktivtVedtakService.oppdaterAktivtVedtak(eksisterendeAktivtVedtak, oppdatertAktivtVedtak)
  }

  private fun opprettNyttAktivtVedtak(
    vedtakHendelse: VedtakHendelse,
    vedtakHendelsePeriode: VedtakHendelsePeriode,
    bidragVedtakData: BidragVedtakData
  ) {

    val nyttAktivtVedtak = AktivtVedtakBo(
      vedtakId = vedtakHendelse.vedtakId,
      sakId = vedtakHendelse.sakId,
      soknadsbarnId = vedtakHendelse.kravhaverId,
      mottakerId = vedtakHendelse.mottakerId,
      vedtakDatoSisteVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(),
      vedtakDatoSisteManuelleVedtak = vedtakHendelse.opprettetTimestamp.toLocalDate(),
      vedtakType = vedtakHendelse.vedtakType.toString(),
      belop = vedtakHendelsePeriode.belop,
      valutakode = vedtakHendelsePeriode.valutakode,
      resultatkode = vedtakHendelsePeriode.resultatkode,
      mottakerSivilstandSisteManuelleVedtak = bidragVedtakData.mottakerSivilstandSisteManuelleVedtak,
      mottakerAntallBarnSisteManuelleVedtak = bidragVedtakData.mottakerAntallBarnSisteManuelleVedtak,
      soknadsbarnBostedsstatus = bidragVedtakData.soknadsbarnBostedsstatus,
      soknadsbarnFodselsdato = bidragVedtakData.soknadsbarnFodselsdato,
      soknadsbarnHarUnntakskode = bidragVedtakData.soknadsbarnHarUnntakskode
    )

    aktivtVedtakService.opprettNyttAktivtVedtak(nyttAktivtVedtak)
  }
}

data class BidragVedtakData(
  val mottakerSivilstandSisteManuelleVedtak: String = "",
  val mottakerAntallBarnSisteManuelleVedtak: Int = 0,
  val soknadsbarnBostedsstatus: String = "",
  val soknadsbarnFodselsdato: LocalDate = LocalDate.now(),
  val soknadsbarnHarUnntakskode: Boolean = false
)
