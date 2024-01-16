package no.nav.eux.oppgave

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.openapitools.SpringDocConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@EnableJwtTokenValidation(
    ignore = [
        "org.springframework",
        "org.springdoc"
    ]
)
@SpringBootApplication
@Import(value = [SpringDocConfiguration::class])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
