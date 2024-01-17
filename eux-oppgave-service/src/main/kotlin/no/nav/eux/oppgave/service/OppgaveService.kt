package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.model.EuxOppgave
import no.nav.eux.oppgave.model.EuxOppgaveOpprettelse
import org.springframework.stereotype.Service

@Service
class OppgaveService(
    val client: OppgaveClient
) {
    val log = logger {}

    fun opprettOppgave(euxOppgaveOpprettelse: EuxOppgaveOpprettelse): EuxOppgave {
        log.info { "Oppretter oppgave. journalpostId=${euxOppgaveOpprettelse.journalpostId}..." }
        val oppgave = client.opprettOppgave(euxOppgaveOpprettelse.oppgaveOpprettelse)
        log.info { "Oppgave opprettet. id=${oppgave.id}, journalpostId=${oppgave.journalpostId}" }
        return oppgave.euxOppgave
    }
}

