package no.nav.bidrag.revurder.forskudd.data

import no.nav.bidrag.behandling.felles.enums.BostatusKode
import no.nav.bidrag.behandling.felles.enums.SivilstandKode
import no.nav.bidrag.behandling.felles.enums.VedtakType
import no.nav.bidrag.revurder.forskudd.data.bo.AktivtVedtakBo
import no.nav.bidrag.revurder.forskudd.data.dto.NyttAktivtVedtakRequestDto
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class TestUtil {

  companion object {

    fun byggAktivtVedtakRequest() = NyttAktivtVedtakRequestDto(
      vedtakId = 1,
      sakId = "SAK-001",
      soknadsbarnId = "01010511111",
      mottakerId = "01018211111",
      vedtakDatoSisteVedtak = LocalDate.parse("2020-01-01"),
      vedtakDatoSisteManuelleVedtak = LocalDate.parse("2020-01-01"),
      vedtakType = VedtakType.MANUELT,
      belop = BigDecimal.valueOf(3490),
      valutakode = "NOK",
      resultatkode = "FORHOYET_FORSKUDD_100_PROSENT",
      mottakerSivilstandSisteManuelleVedtak = SivilstandKode.GIFT,
      mottakerAntallBarnSisteManuelleVedtak = 2,
      soknadsbarnBostedsstatus = BostatusKode.MED_FORELDRE,
      soknadsbarnFodselsdato = LocalDate.parse("2015-01-01"),
      soknadsbarnHarUnntakskode = false
    )

    fun byggAktivtVedtakDto() = AktivtVedtakBo(
      aktivtVedtakId = (1..100).random(),
      vedtakId = 1,
      sakId = "SAK-001",
      soknadsbarnId = "01010511111",
      mottakerId = "01018211111",
      vedtakDatoSisteVedtak = LocalDate.parse("2020-01-01"),
      vedtakDatoSisteManuelleVedtak = LocalDate.parse("2020-01-01"),
      vedtakType = VedtakType.MANUELT,
      belop = BigDecimal.valueOf(3490),
      valutakode = "NOK",
      resultatkode = "FORHOYET_FORSKUDD_100_PROSENT",
      mottakerSivilstandSisteManuelleVedtak = SivilstandKode.GIFT,
      mottakerAntallBarnSisteManuelleVedtak = 2,
      soknadsbarnBostedsstatus = BostatusKode.MED_FORELDRE,
      soknadsbarnFodselsdato = LocalDate.parse("2015-01-01"),
      soknadsbarnHarUnntakskode = false,
      opprettetTimestamp = LocalDateTime.now()
    )
  }
}
