package no.nav.eux.oppgave

import org.openapitools.SpringDocConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(value = [SpringDocConfiguration::class])
@EnableConfigurationProperties(DataSourceProperties::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
