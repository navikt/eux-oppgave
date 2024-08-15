package no.nav.eux.oppgave.advice

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.model.exception.OppgaveStatusEksistererException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class OppgaveEksistererAdvice {

    val log = logger {}

    @ExceptionHandler(value = [OppgaveStatusEksistererException::class])
    fun oppgaveUgyldigRequestException(exception: OppgaveStatusEksistererException) =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(exception.apiError)
            .also { log.error { "Forsøk på duplikat oppgaveopprettelse" } }

    val OppgaveStatusEksistererException.apiError
        get() = ApiError(
            message = message
        )

    data class ApiError(
        val message: String
    )
}
