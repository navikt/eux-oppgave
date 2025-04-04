package no.nav.eux.oppgave.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.client.OppgaveClient
import no.nav.eux.oppgave.integration.model.patch.OppgavePatchKommentar
import no.nav.eux.oppgave.integration.model.patch.OppgaveTypePatch
import org.springframework.stereotype.Service

@Service
class OppgavetypeService(
    val client: OppgaveClient,
    val contextService: TokenContextService,
) {
    val log = logger {}

    fun patch(ider: List<Int>, oppgavetype: String, kommentar: String?) {
        log.info { "Starter oppdatering av ${ider.size} oppgaver..." }
        ider.forEach { patch(it, oppgavetype, kommentar) }
        log.info { "Oppgaver oppdatert" }
    }

    fun patch(id: Int, oppgavetype: String, kommentar: String?) {
        val eksisterendeOppgave = client.finn(id)
        val nyOppgaveBeskrivelse = OppgaveBeskrivelse(
            navIdent = contextService.navIdent,
            tekst = kommentar ?: "Oppgavetype endret til $oppgavetype"
        )
        val patch = OppgaveTypePatch(
            versjon = eksisterendeOppgave.versjon,
            oppgavetype = oppgavetype,
            kommentar = OppgavePatchKommentar(
                tekst = kommentar ?: "Oppgavetype endret til $oppgavetype"
            ),
            beskrivelse = eksisterendeOppgave.beskrivelse med nyOppgaveBeskrivelse
        )
        try {
            client.patch(id, patch)
            log.info { "Oppdaterte oppgave $id til $oppgavetype med kommentar: $kommentar" }
        } catch (e: Exception) {
            log.error(e) { "Kunne ikke oppdatere oppgave $id til $oppgavetype" }
        }
    }

}
