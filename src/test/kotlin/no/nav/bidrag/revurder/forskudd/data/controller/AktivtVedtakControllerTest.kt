package no.nav.bidrag.revurder.forskudd.data.controller

import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate
import no.nav.bidrag.revurder.forskudd.data.BidragRevurderForskuddDataTest
import no.nav.bidrag.revurder.forskudd.data.PROFILE_TEST
import no.nav.bidrag.revurder.forskudd.data.TestUtil
import no.nav.bidrag.revurder.forskudd.data.dto.FinnAktivtVedtakDto
import no.nav.bidrag.revurder.forskudd.data.dto.NyttAktivtVedtakRequestDto
import no.nav.bidrag.revurder.forskudd.data.persistence.repository.AktivtVedtakRepository
import no.nav.bidrag.revurder.forskudd.data.service.PersistenceService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.util.UriComponentsBuilder

@DisplayName("AktivtVedtakControllerTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [BidragRevurderForskuddDataTest::class])
@ActiveProfiles(PROFILE_TEST)
//@AutoConfigureWireMock(port = 0)
class AktivtVedtakControllerTest {

  @Autowired
  private lateinit var testRestTemplate: HttpHeaderTestRestTemplate

  @Autowired
  private lateinit var aktivtVedtakRepository: AktivtVedtakRepository

  @Autowired
  private lateinit var persistenceService: PersistenceService

  @LocalServerPort
  private val port = 0

  @BeforeEach
  fun `init`() {
    // Sletter alle forekomster
    aktivtVedtakRepository.deleteAll()
  }

  @Test
  fun `skal mappe til context path med random port`() {
    assertThat(makeFullContextPath()).isEqualTo("http://localhost:$port")
  }

  @Disabled
  @Test
  fun `skal opprette nytt aktivt vedtak`() {

    // Oppretter ny forekomst av aktivt vedtak
    val response = testRestTemplate.exchange(
      fullUrlForNyttAktivtVedtak(),
      HttpMethod.POST,
      byggAktivtVedtakRequest(),
      Int::class.java
    )

    assertAll(
      Executable { assertThat(response).isNotNull() },
      Executable { assertThat(response?.statusCode).isEqualTo(HttpStatus.OK) },
      Executable { assertThat(response?.body).isNotNull() },
    )
  }

  @Disabled
  @Test
  fun `skal finne data for et aktivt vedtak`() {
    // Oppretter ny forekomst av aktivt vedtak
    val nyttAktivtVedtakOpprettet = persistenceService.opprettNyttAktivtVedtak(TestUtil.byggAktivtVedtakDto())

    // Henter forekomst
    val response = testRestTemplate.exchange(
      "${fullUrlForSokAktivtVedtak()}/${nyttAktivtVedtakOpprettet.aktivtVedtakId}",
      HttpMethod.GET,
      null,
      FinnAktivtVedtakDto::class.java
    )

    assertAll(
      Executable { assertThat(response).isNotNull() },
      Executable { assertThat(response?.statusCode).isEqualTo(HttpStatus.OK) },
      Executable { assertThat(response?.body).isNotNull },
      Executable { assertThat(response?.body?.vedtakId).isEqualTo(1) },
      Executable { assertThat(response?.body?.sakId).isEqualTo("SAK-001") },
      Executable { assertThat(response?.body?.soknadsbarnId).isEqualTo("01010511111") },
      Executable { assertThat(response?.body?.mottakerId).isEqualTo("01018211111") },
    )
  }

  private fun fullUrlForNyttAktivtVedtak(): String {
    return UriComponentsBuilder.fromHttpUrl(makeFullContextPath() + AktivtVedtakController.AKTIVT_VEDTAK_NY).toUriString()
  }

  private fun fullUrlForSokAktivtVedtak(): String {
    return UriComponentsBuilder.fromHttpUrl(makeFullContextPath() + AktivtVedtakController.AKTIVT_VEDTAK_SOK).toUriString()
  }

  private fun makeFullContextPath(): String {
    return "http://localhost:$port"
  }

  private fun byggAktivtVedtakRequest(): HttpEntity<NyttAktivtVedtakRequestDto> {
    return initHttpEntity(TestUtil.byggAktivtVedtakRequest())
  }

  private fun <T> initHttpEntity(body: T): HttpEntity<T> {
    val httpHeaders = HttpHeaders()
    httpHeaders.contentType = MediaType.APPLICATION_JSON
    return HttpEntity(body, httpHeaders)
  }
}
