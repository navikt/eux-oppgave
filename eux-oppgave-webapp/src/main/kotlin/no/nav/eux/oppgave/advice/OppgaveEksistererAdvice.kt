package no.nav.eux.oppgave.advice

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.model.exception.OppgaveStatusEksistererException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.time.LocalDateTime.now

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
            timestamp = now(),
            message = message
        )

    data class ApiError(
        val timestamp: LocalDateTime,
        val message: String
    )
}
