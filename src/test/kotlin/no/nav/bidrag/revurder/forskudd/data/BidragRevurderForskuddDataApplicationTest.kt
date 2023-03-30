package no.nav.bidrag.revurder.forskudd.data

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.ActiveProfiles

// @ExtendWith(SpringExtension::class)
@DisplayName("BidragRevurderForskuddData")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = [BidragRevurderForskuddDataTest::class])
@ActiveProfiles(PROFILE_TEST)
class BidragRevurderForskuddDataApplicationTest {

    @Test
    fun `skal laste spring-context`() {
    }
}
