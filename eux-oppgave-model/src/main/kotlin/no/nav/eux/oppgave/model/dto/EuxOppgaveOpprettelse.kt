package no.nav.eux.oppgave.model.dto

import java.time.LocalDate
import java.util.*

data class EuxOppgaveOpprettelse(
    val oppgaveUuid: UUID?,
    val aktoerId: String?,
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
    val metadata: Map<String, String>,
    val lagNestenLikOppgave: Boolean
)
