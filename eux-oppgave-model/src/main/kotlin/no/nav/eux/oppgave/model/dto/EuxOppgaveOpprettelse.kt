package no.nav.eux.oppgave.model.dto

import java.time.LocalDate

data class EuxOppgaveOpprettelse(
    val aktivDato: LocalDate,
    val prioritet: EuxOppgavePrioritetEnum,
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
    val behandlesAvApplikasjon: String?,
    val opprettetBruker: String,
)