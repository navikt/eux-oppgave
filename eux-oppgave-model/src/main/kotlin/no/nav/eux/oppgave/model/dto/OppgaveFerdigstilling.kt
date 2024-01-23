package no.nav.eux.oppgave.model.dto

data class OppgaveFerdigstilling(
    val euxOppgave: EuxOppgave? = null,
    val status: OppgaveFerdigstillingStatus,
    val beskrivelse: String = "",
)

enum class OppgaveFerdigstillingStatus {
    OPPGAVE_FERDIGSTILT,
    FERDIGSTILLING_FEILET
}
