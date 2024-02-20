package no.nav.eux.oppgave.service

import org.slf4j.MDC

fun <T> T.mdc(
    rinasakId: Int? = null,
    dokumentInfoId: String? = null,
    journalpostId: String? = null
): T {
    "rinasakId" leggTil rinasakId
    "dokumentInfoId" leggTil dokumentInfoId
    "journalpostId" leggTil journalpostId
    return this
}

private infix fun String.leggTil(value: Any?) {
    if (value != null) MDC.put(this, "$value")
}
