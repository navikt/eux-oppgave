package no.nav.eux.oppgave.integration.client

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import java.lang.Thread.sleep

@Service
class OpprettOppgaveRetryableClient(
    val oppgaveClient: OppgaveClient
) {

    val log = logger {}

    fun opprettUnikOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave =
        if (oppgaveOpprettelse.journalpostId != null && oppgaveOpprettelse.oppgavetype != null)
            opprettUnikOppgaveRetryable(
                oppgaveOpprettelse = oppgaveOpprettelse,
                journalpostId = oppgaveOpprettelse.journalpostId,
                oppgavetype = oppgaveOpprettelse.oppgavetype,
            )
        else
            oppgaveClient.opprettOppgave(oppgaveOpprettelse)

    private fun opprettUnikOppgaveRetryable(
        oppgaveOpprettelse: OppgaveOpprettelse,
        journalpostId: String,
        oppgavetype: String,
        retries: Int = 4
    ): Oppgave {
        val eksisterendeOppgaver = oppgaveClient
            .hentOppgaver(
                journalpostId = journalpostId,
                oppgavetype = listOf(oppgavetype),
            )
        return if (eksisterendeOppgaver.isEmpty())
            tryOpprettOppgave(
                oppgaveOpprettelse = oppgaveOpprettelse,
                retries = retries,
                journalpostId = journalpostId,
                oppgavetype = oppgavetype
            )
        else
            eksisterendeOppgaver.first()
    }

    private fun tryOpprettOppgave(
        oppgaveOpprettelse: OppgaveOpprettelse,
        retries: Int,
        journalpostId: String,
        oppgavetype: String
    ) = try {
        oppgaveClient.opprettOppgave(oppgaveOpprettelse)
    } catch (e: HttpServerErrorException) {
        log.warn(e) { "Oppgaveopprettelse feilet, forsøker igjen, forsøk igjen: $retries" }
        if (retries > 0) {
            sleep(1000)
            opprettUnikOppgaveRetryable(
                oppgaveOpprettelse = oppgaveOpprettelse,
                journalpostId = journalpostId,
                oppgavetype = oppgavetype,
                retries = retries - 1
            )
        } else {
            log.error(e) { "Oppgaveopprettelse feilet, maks antall forsøk nådd" }
            throw e
        }
    } catch (e: Exception) {
        log.error(e) { "Oppgaveopprettelse feilet" }
        throw e
    }
}
