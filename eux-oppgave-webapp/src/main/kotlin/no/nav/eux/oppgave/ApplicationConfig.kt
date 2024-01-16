package no.nav.eux.oppgave

import com.zaxxer.hikari.HikariDataSource
import no.nav.eux.logging.RequestIdMdcFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig(
    val dataSourceProperties: DataSourceProperties
) {

    @Bean
    fun hikariDataSource() = HikariDataSource(dataSourceProperties.hikari)

    @Bean
    fun requestIdMdcFilter() = RequestIdMdcFilter()

}
