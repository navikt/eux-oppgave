package no.nav.eux.oppgave.webapp

import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity

fun <T : Any> T.toOkResponseEntity() = ResponseEntity(this, OK)

fun <T : Any> T.toCreatedEmptyResponseEntity() = ResponseEntity(this, CREATED)

fun <T : Any> T.toCreatedResponseEntity() = ResponseEntity(this, CREATED)

fun Any.toEmptyResponseEntity() = ResponseEntity.noContent().build<Unit>()
