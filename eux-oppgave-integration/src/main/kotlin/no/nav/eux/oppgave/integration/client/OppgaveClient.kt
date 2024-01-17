package no.nav.eux.oppgave.integration.client

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.config.get
import no.nav.eux.oppgave.integration.config.patch
import no.nav.eux.oppgave.integration.config.post
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import no.nav.eux.oppgave.integration.model.OppgavePatch
import no.nav.eux.oppgave.integration.model.Oppgaver
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.toEntity
import org.springframework.web.util.UriBuilder

@Component
class OppgaveClient(
    @Value("\${endpoint.oppgave}")
    val oppgaveUrl: String,
    val oppgaveRestTemplate: RestTemplate
) {
    val log = logger {}

    fun opprettOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave {
        val entity: ResponseEntity<Oppgave> = oppgaveRestTemplate
            .post
            .uri("${oppgaveUrl}/api/v1/oppgaver")
            .contentType(APPLICATION_JSON)
            .body(oppgaveOpprettelse)
            .retrieve()
            .toEntity()
        when {
            entity.statusCode.is2xxSuccessful -> return entity.body!!
            else -> throw opprettelseException(oppgaveOpprettelse, entity)
        }
    }

    fun patch(id: Int, patch: OppgavePatch): Oppgave {
        val entity: ResponseEntity<Oppgave> = oppgaveRestTemplate
            .patch
            .uri("${oppgaveUrl}/api/v1/oppgaver/$id")
            .contentType(APPLICATION_JSON)
            .body(patch)
            .retrieve()
            .toEntity()
        when {
            entity.statusCode.is2xxSuccessful -> return entity.body!!
            else -> throw patchException(id, entity)
        }
    }

    fun hentOppgave(id: String): Oppgave {
        val entity: ResponseEntity<Oppgave> = oppgaveRestTemplate
            .get
            .uri("${oppgaveUrl}/api/v1/oppgaver/$id")
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity()
        when {
            entity.statusCode.is2xxSuccessful -> return entity.body!!
            else -> throw hentOppgaveException(id, entity)
        }
    }

    fun hentOppgaver(journalpostId: String): List<Oppgave> {
        val entity: ResponseEntity<Oppgaver> = oppgaveRestTemplate
            .get
            .uri { it.uri(journalpostId) }
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity()
        when {
            entity.statusCode.is2xxSuccessful -> return entity.body!!.oppgaver
            else -> throw hentException(journalpostId, entity)
        }
    }

    fun UriBuilder.uri(journalpostId: String) =
        path("${oppgaveUrl}/api/v1/oppgaver")
            .queryParam("journalpostId", journalpostId)
            .build()

    fun opprettelseException(
        oppgaveOpprettelse: OppgaveOpprettelse,
        entity: ResponseEntity<Oppgave>
    ): RuntimeException {
        val msg = "Feil under opprettelse av oppgave. journalpostId=${oppgaveOpprettelse.journalpostId}"
        log.error { "$msg, body=${entity.body}" }
        return RuntimeException(msg)
    }

    fun patchException(
        id: Int,
        entity: ResponseEntity<Oppgave>
    ): RuntimeException {
        val msg = "Feil under patching av oppgaven. id=$id"
        log.error { "$msg, body=${entity.body}" }
        return RuntimeException(msg)
    }

    fun hentException(
        journalpostId: String,
        entity: ResponseEntity<Oppgaver>
    ): RuntimeException {
        val msg = "Feil under henting av oppgaver. journalpostId=$journalpostId"
        log.error { "$msg, body=${entity.body}" }
        return RuntimeException(msg)
    }

    fun hentOppgaveException(
        journalpostId: String,
        entity: ResponseEntity<Oppgave>
    ): RuntimeException {
        val msg = "Feil under henting av oppgave. id=$journalpostId"
        log.error { "$msg, body=${entity.body}" }
        return RuntimeException(msg)
    }
}
