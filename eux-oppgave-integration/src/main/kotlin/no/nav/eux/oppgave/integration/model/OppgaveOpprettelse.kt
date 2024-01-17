package no.nav.eux.oppgave.integration.model

data class OppgaveOpprettelse(
    val aktoerId: String,
    val aktivDato: String,
    val prioritet: Prioritet,
    val tema: String,
    val oppgavetype: String?,
    val behandlingstema: String?,
    val behandlingstype: String?,
    val journalpostId: String?,
    val saksreferanse: String?,
    val tildeltEnhetsnr: String?,
    val beskrivelse: String?,
    val fristFerdigstillelse: java.time.LocalDate?,
    val opprettetAvEnhetsnr: String?
)
