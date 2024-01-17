package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgavePatch
import no.nav.eux.oppgave.integration.model.Status.FERDIGSTILT
import no.nav.eux.oppgave.model.EuxOppgave
import org.springframework.stereotype.Service

@Service
class FerdigstillService(
    val client: OppgaveClient
) {
    val log = logger {}

    fun ferdigstillOppgaver(journalpostIder: List<String>): List<EuxOppgave> =
        journalpostIder
            .also { log.info { "Starter ferdigstilling av journalposter: $journalpostIder" } }
            .mapNotNull { velgOppgave(it, client.hentOppgaver(it)) }
            .map { client.patch(it.id, OppgavePatch(it.versjon, FERDIGSTILT)) }
            .map { it.euxOppgave }

    fun velgOppgave(journalpostId: String, oppgaver: List<Oppgave>): Oppgave? {
        val valgtOppgave = oppgaver.firstOrNull { it.status != FERDIGSTILT }
        log.info { "Ferdigstiller ${valgtOppgave?.id}" }
        return valgtOppgave
    }
}
