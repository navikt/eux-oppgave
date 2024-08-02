package no.nav.eux.oppgave.advice

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveUgyldigRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class OppgaveUgyldigRequestAdvice {

    val log = logger {}

    @ExceptionHandler(value = [OppgaveUgyldigRequestException::class])
    fun oppgaveUgyldigRequestException(exception: OppgaveUgyldigRequestException) =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(exception.oppgaveUgyldigRequest)
            .also { log.error { "Kall mot oppgave feilet: ${exception.oppgaveUgyldigRequest.feilmelding}" } }
}
