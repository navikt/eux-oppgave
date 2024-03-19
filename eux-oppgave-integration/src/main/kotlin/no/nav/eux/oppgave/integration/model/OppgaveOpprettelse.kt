package no.nav.eux.oppgave.integration.model

import java.time.LocalDate

data class OppgaveOpprettelse(
    val aktoerId: String?,
    val aktivDato: LocalDate,
    val prioritet: Prioritet,
    val tema: String,
    val oppgavetype: String?,
    val behandlingstema: String?,
    val behandlingstype: String?,
    val journalpostId: String?,
    val saksreferanse: String?,
    val tildeltEnhetsnr: String?,
    val beskrivelse: String?,
    val fristFerdigstillelse: LocalDate?,
    val opprettetAvEnhetsnr: String?,
    val metadata: Map<String, String>,
)
