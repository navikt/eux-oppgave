package no.nav.eux.oppgave.webapp.model

import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType

data class TestModelOppgaveFerdigstilling(
    val status: TestModelFerdigstillingStatus,
    val oppgave: OppgaveOpenApiType?,
    val beskrivelse: String?
)

enum class TestModelFerdigstillingStatus {
    OPPGAVE_FERDIGSTILT,
    FERDIGSTILLING_FEILET
}
