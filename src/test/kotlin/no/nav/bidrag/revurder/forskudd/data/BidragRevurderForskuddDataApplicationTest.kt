package no.nav.bidrag.revurder.forskudd.data

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = [BidragRevurderForskuddDataTest::class])
@ActiveProfiles(PROFILE_TEST)
@DisplayName("BidragRevurderForskuddData")
class BidragRevurderForskuddDataApplicationTest {

  @Test
  fun `skal laste spring-context`() {
  }
}
