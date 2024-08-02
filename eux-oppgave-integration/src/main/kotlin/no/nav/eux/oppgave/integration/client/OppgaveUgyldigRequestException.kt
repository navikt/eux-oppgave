package no.nav.eux.oppgave.integration.client

import java.util.*

data class OppgaveUgyldigRequest(
    val uuid: UUID,
    val feilmelding: String
)

class OppgaveUgyldigRequestException(
    val oppgaveUgyldigRequest: OppgaveUgyldigRequest,
    override val message: String = "",
) : RuntimeException(message)
