package no.nav.eux.oppgave.integration.client

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.oppgave.integration.config.DualOppgaveRestTemplate
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import no.nav.eux.oppgave.integration.model.Oppgaver
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.toEntity
import org.springframework.web.util.UriComponentsBuilder

@Component
class OppgaveClient(
    @Value("\${endpoint.oppgave}")
    val oppgaveUrl: String,
    val dualOppgaveRestTemplate: DualOppgaveRestTemplate
) {
    val log = logger {}

    fun opprettOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave {
        val entity: ResponseEntity<Oppgave> = dualOppgaveRestTemplate
            .post()
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

    fun patch(id: Int, patch: Any): Oppgave {
        val entity: ResponseEntity<Oppgave> = dualOppgaveRestTemplate
            .patch()
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
        val entity: ResponseEntity<Oppgave> = dualOppgaveRestTemplate
            .get()
            .uri("${oppgaveUrl}/api/v1/oppgaver/$id")
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity()
        when {
            entity.statusCode.is2xxSuccessful -> return entity.body!!
            else -> throw hentOppgaveException(id, entity)
        }
    }

    fun hentOppgaver(
        journalpostId: String,
        oppgavetype: List<String> = listOf("JFR", "FDR"),
        statuskategori: String? = "AAPEN"
    ): List<Oppgave> {
        val entity: ResponseEntity<Oppgaver> = dualOppgaveRestTemplate
            .get()
            .uri(hentOppgaverUriOptionalStatuskategori(journalpostId, oppgavetype, statuskategori))
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity()
        when {
            entity.statusCode.is2xxSuccessful -> return entity.body!!.oppgaver
            else -> throw hentException(journalpostId, entity)
        }
    }

    fun hentOppgaverUriOptionalStatuskategori(
        journalpostId: String,
        oppgavetype: List<String>,
        statuskategori: String?
    ) =
        when {
            statuskategori == null -> hentOppgaverUri(journalpostId, oppgavetype)
            else -> hentOppgaverUri(journalpostId, oppgavetype, statuskategori)
        }

    fun hentOppgaverUri(
        journalpostId: String,
        oppgavetype: List<String>
    ) =
        UriComponentsBuilder
            .fromHttpUrl("${oppgaveUrl}/api/v1/oppgaver")
            .queryParam("journalpostId", journalpostId)
            .queryParam("oppgavetype", oppgavetype)
            .build()
            .toUri()

    fun hentOppgaverUri(
        journalpostId: String,
        oppgavetype: List<String>,
        statuskategori: String
    ) =
        UriComponentsBuilder
            .fromHttpUrl("${oppgaveUrl}/api/v1/oppgaver")
            .queryParam("journalpostId", journalpostId)
            .queryParam("statuskategori", statuskategori)
            .queryParam("oppgavetype", oppgavetype)
            .build()
            .toUri()

    fun opprettelseException(
        oppgaveOpprettelse: OppgaveOpprettelse,
        entity: ResponseEntity<Oppgave>
    ) = oppgaveException(
        "Feil under opprettelse av oppgave. journalpostId=${oppgaveOpprettelse.journalpostId}", entity.body
    )

    fun patchException(
        id: Int,
        entity: ResponseEntity<Oppgave>
    ) = oppgaveException("Feil under patching av oppgaven. id=$id", entity.body)

    fun hentException(
        journalpostId: String,
        entity: ResponseEntity<Oppgaver>
    ) = oppgaveException("Feil under henting av oppgaver. journalpostId=$journalpostId", entity.body)

    fun hentOppgaveException(
        journalpostId: String,
        entity: ResponseEntity<Oppgave>
    ) = oppgaveException("Feil under henting av oppgave. id=$journalpostId", entity.body)

    fun oppgaveException(
        msg: String,
        body: Any?
    ): RuntimeException {
        log.error { "$msg, body=$body" }
        return RuntimeException(msg)
    }
}
