package no.nav.eux.oppgave.model

import java.time.LocalDate
import java.time.OffsetDateTime

data class EuxOppgave(
    val id: Int,
    val oppgavetype: String,
    val tildeltEnhetsnr: String,
    val aktivDato: LocalDate,
    val euxOppgavePrioritet: EuxOppgavePrioritet,
    val versjon: Int,
    val tema: String,
    val aktoerId: String?,
    val behandlingstema: String?,
    val behandlingstype: String?,
    val journalpostId: String?,
    val saksreferanse: String?,
    val beskrivelse: String?,
    val fristFerdigstillelse: LocalDate?,
    val opprettetAvEnhetsnr: String?,
    val euxOppgaveStatus: EuxOppgaveStatus?,
    val opprettetTidspunkt: OffsetDateTime?,
    val ferdigstiltTidspunkt: OffsetDateTime?,
    val endretTidspunkt: OffsetDateTime?,
    val behandlesAvApplikasjon: String?,
)
