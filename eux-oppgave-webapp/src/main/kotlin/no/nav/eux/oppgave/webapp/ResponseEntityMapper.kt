package no.nav.eux.oppgave.webapp

import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity

fun <T> T.toOkResponseEntity() = ResponseEntity<T>(this, OK)

fun <T> T.toCreatedEmptyResponseEntity() = ResponseEntity<T>(this, CREATED)

fun <T> T.toCreatedResponseEntity() = ResponseEntity<T>(this, CREATED)

fun Any.toEmptyResponseEntity() = ResponseEntity.noContent().build<Unit>()
