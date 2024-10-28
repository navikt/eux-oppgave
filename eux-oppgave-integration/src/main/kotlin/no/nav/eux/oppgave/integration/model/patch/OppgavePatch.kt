package no.nav.eux.oppgave.integration.model.patch

import no.nav.eux.oppgave.integration.model.Status

data class OppgavePatch(
    val versjon: Int,
    val tilordnetRessurs: String?,
    val status: Status? = null,
    val personident: String?
)
