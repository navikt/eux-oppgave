package no.nav.eux.oppgave.integration.model

data class OppgavePatch(
    val versjon: Int,
    val tilordnetRessurs: String,
    val status: Status? = null,
    val personident: String?
)
