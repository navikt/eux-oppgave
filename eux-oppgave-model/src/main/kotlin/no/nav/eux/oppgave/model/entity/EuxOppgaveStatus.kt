package no.nav.eux.oppgave.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
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
        FERDIGSTILLING_FEILET
    }
}
