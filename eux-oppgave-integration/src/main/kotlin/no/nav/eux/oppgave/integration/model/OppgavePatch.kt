package no.nav.eux.oppgave.integration.model

data class OppgavePatch(
    val versjon: Int,
    val status: Status? = null,
)
