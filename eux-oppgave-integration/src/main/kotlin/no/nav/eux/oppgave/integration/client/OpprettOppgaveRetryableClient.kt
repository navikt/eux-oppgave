package no.nav.eux.oppgave.integration.client

import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import org.springframework.stereotype.Service

@Service
class OpprettOppgaveRetryableClient(
    val oppgaveClient: OppgaveClient
) {
    
    fun opprettUnikOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave =
        if (oppgaveOpprettelse.journalpostId != null && oppgaveOpprettelse.oppgavetype != null) {
            val eksisterendeOppgaver = oppgaveClient
                .hentOppgaver(
                    journalpostId = oppgaveOpprettelse.journalpostId,
                    oppgavetype = listOf(oppgaveOpprettelse.oppgavetype),
                )
            if (eksisterendeOppgaver.isNotEmpty()) {
                oppgaveClient.opprettOppgave(oppgaveOpprettelse)
            } else {
                throw NestenLikOppgaveEksistererException()
            }
        } else {
            oppgaveClient.opprettOppgave(oppgaveOpprettelse)
        }
}

class NestenLikOppgaveEksistererException(
    override val message: String = "Det eksisterer allerede en oppgave med gitt journalpostId og oppgavetype",
) : RuntimeException(message)