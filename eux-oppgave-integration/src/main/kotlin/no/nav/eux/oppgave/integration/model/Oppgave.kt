package no.nav.eux.oppgave.integration.model

import java.time.LocalDate
import java.time.OffsetDateTime

data class Oppgave(
    val id: Int,
    val oppgavetype: String,
    val tildeltEnhetsnr: String,
    val aktivDato: LocalDate,
    val prioritet: Prioritet,
    val versjon: Int,
    val aktoerId: String? = null,
    val behandlingstema: String? = null,
    val behandlingstype: String? = null,
    val journalpostId: String? = null,
    val saksreferanse: String? = null,
    val beskrivelse: String? = null,
    val fristFerdigstillelse: LocalDate? = null,
    val opprettetAvEnhetsnr: String? = null,
    val status: Status,
    val tema: String,
    val opprettetTidspunkt: OffsetDateTime? = null,
    val ferdigstiltTidspunkt: OffsetDateTime? = null,
    val endretTidspunkt: OffsetDateTime?,
    val behandlesAvApplikasjon: String?
)
