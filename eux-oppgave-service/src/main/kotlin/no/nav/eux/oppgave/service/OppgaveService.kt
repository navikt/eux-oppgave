package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.model.dto.EuxOppgave
import no.nav.eux.oppgave.model.dto.EuxOppgaveOpprettelse
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.OPPRETTELSE_FEILET
import no.nav.eux.oppgave.model.entity.EuxOppgaveStatus.Status.OPPRETTET
import no.nav.eux.oppgave.persistence.EuxOppgaveStatusRepository
import org.springframework.stereotype.Service

@Service
class OppgaveService(
    val client: OppgaveClient,
    val exuOppgaveStatusRepository: EuxOppgaveStatusRepository,
) {
    val log = logger {}

    fun opprettOppgave(euxOppgaveOpprettelse: EuxOppgaveOpprettelse): EuxOppgave {
        log.info { "Oppretter oppgave. journalpostId=${euxOppgaveOpprettelse.journalpostId}..." }
        val euxOppgaveStatus = exuOppgaveStatusRepository.save(euxOppgaveOpprettelse.euxOppgaveStatus)
        try {
            val oppgave = client.opprettOppgave(euxOppgaveOpprettelse.oppgaveOpprettelse)
            exuOppgaveStatusRepository.save(euxOppgaveStatus.copy(oppgaveId = oppgave.id, status = OPPRETTET))
            log.info { "Oppgave opprettet. id=${oppgave.id}, journalpostId=${oppgave.journalpostId}" }
            return oppgave.euxOppgave
        } catch (e: Exception) {
            log.info { "Oppgave opprettelse feilet. journalpostId=${euxOppgaveOpprettelse.journalpostId}" }
            exuOppgaveStatusRepository.save(euxOppgaveStatus.copy(status = OPPRETTELSE_FEILET))
            throw e
        }
    }
}
