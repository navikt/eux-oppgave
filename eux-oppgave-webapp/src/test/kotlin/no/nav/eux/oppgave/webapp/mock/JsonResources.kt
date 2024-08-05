package no.nav.eux.oppgave.webapp.mock

val oppgaverResponse = getResource("/dataset/response/oppgave.json")

val oppgaverResponseFeilmelding = getResource("/dataset/response/oppgave-feilmelding.json")

val getOppgaverResponse = getResource("/dataset/response/oppgaver.json")

fun getResource(resource: String) = Any::class::class.java.getResource(resource)!!.readText()
