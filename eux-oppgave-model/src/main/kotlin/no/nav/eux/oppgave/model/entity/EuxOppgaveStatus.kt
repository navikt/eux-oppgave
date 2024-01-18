package no.nav.eux.oppgave.model.entity

import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

@Entity
data class EuxOppgaveStatus(
    @Id
    val oppgaveUuid: UUID = UUID.randomUUID(),
    val oppgaveId: Int? = null,
    val tema: String,
    @Enumerated(STRING)
    val status: Status,
    val beskrivelse: String?,
    @Column(updatable = false)
    val opprettetBruker: String,
    @Column(updatable = false)
    val opprettetTidspunkt: LocalDateTime = now(),
    @Column
    val endretTidspunkt: LocalDateTime = now(),
) {

    enum class Status {
        UNDER_OPPRETTELSE,
        OPPRETTELSE_FEILET,
        OPPRETTET,
        UNDER_FERDIGSTILLING,
        FERDIGSTILT,
    }
}