package no.nav.bidrag.revurder.forskudd.data

import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
@EnableAspectJAutoProxy
class BidragRevurderForskuddData

fun main(args: Array<String>) {
    val profile = if (args.isEmpty()) LIVE_PROFILE else args[0]
    val app = SpringApplication(BidragRevurderForskuddData::class.java)
    app.setAdditionalProfiles(profile)
    app.run(*args)
}
