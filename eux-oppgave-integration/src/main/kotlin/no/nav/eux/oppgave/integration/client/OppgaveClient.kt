package no.nav.eux.oppgave.integration.client

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.toEntity
import java.util.*

@Component
class OppgaveClient(
    @Value("\${endpoint.oppgave}")
    val oppgaveUrl: String,
    val oppgaveRestTemplate: RestTemplate
) {
    val log = logger {}

    fun opprettOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave {
        val entity: ResponseEntity<Oppgave> = RestClient.create(oppgaveRestTemplate)
            .post()
            .uri("${oppgaveUrl}/api/v1/oppgaver")
            .contentType(APPLICATION_JSON)
            .body(oppgaveOpprettelse)
            .header("X-Correlation-ID", UUID.randomUUID().toString())
            .retrieve()
            .toEntity()
        if (entity.body == null) {
            val msg = "Ingen body fra oppgave under opprettelse. journalpostId=${oppgaveOpprettelse.journalpostId}"
            log.error { msg }
            throw RuntimeException(msg)
        } else if (entity.statusCode.is2xxSuccessful) {
            return entity.body!!
        } else {
            val msg = "Feil under opprettelse av oppgave. journalpostId=${oppgaveOpprettelse.journalpostId}"
            log.error { "$msg, body=${entity.body}" }
            throw RuntimeException(msg)
        }
    }
}
