package no.nav.bidrag.revurder.forskudd.data

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile(LIVE_PROFILE)
class FlywayConfiguration @Autowired constructor(@Qualifier("dataSource") dataSource: DataSource?) {

    init {
        Thread.sleep(30000)
        Flyway.configure().dataSource(dataSource).load().migrate()
    }
}
