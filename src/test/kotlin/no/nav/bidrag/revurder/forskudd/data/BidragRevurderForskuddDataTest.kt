package no.nav.bidrag.revurder.forskudd.data

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.test.context.ActiveProfiles

// @ComponentScan(excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [BidragRevurderForskuddData::class, BidragRevurderForskuddDataTest::class])])
// @EnableAspectJAutoProxy
@SpringBootApplication
@ActiveProfiles(PROFILE_TEST)
class BidragRevurderForskuddDataTest

fun main(args: Array<String>) {
    val profile = if (args.isEmpty()) PROFILE_TEST else args[0]
    val app = SpringApplication(BidragRevurderForskuddDataTest::class.java)
    app.setAdditionalProfiles(profile)
    app.run(*args)
}
