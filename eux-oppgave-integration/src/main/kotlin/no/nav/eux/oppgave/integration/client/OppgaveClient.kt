package no.nav.eux.oppgave.integration.client

import no.nav.eux.oppgave.integration.config.DualOppgaveRestTemplate
import no.nav.eux.oppgave.integration.model.Oppgave
import no.nav.eux.oppgave.integration.model.OppgaveOpprettelse
import no.nav.eux.oppgave.integration.model.Oppgaver
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.toEntity
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDate

@Component
class OppgaveClient(
    @Value("\${endpoint.oppgave}")
    val oppgaveUrl: String,
    val dualOppgaveRestTemplate: DualOppgaveRestTemplate
) {

    fun opprettOppgave(oppgaveOpprettelse: OppgaveOpprettelse): Oppgave = tryWithOppgaveErrorHandling {
        dualOppgaveRestTemplate
            .post()
            .uri("${oppgaveUrl}/api/v1/oppgaver")
            .contentType(APPLICATION_JSON)
            .body(oppgaveOpprettelse)
            .retrieve()
            .toEntity<Oppgave>()
            .body!!
    }

    fun patch(id: Int, patch: Any): Oppgave = tryWithOppgaveErrorHandling {
        dualOppgaveRestTemplate
            .patch()
            .uri("${oppgaveUrl}/api/v1/oppgaver/$id")
            .contentType(APPLICATION_JSON)
            .body(patch)
            .retrieve()
            .toEntity<Oppgave>()
            .body!!
    }

    fun hentOppgaver(
        journalpostId: String,
        oppgavetype: List<String> = listOf("JFR", "FDR"),
        statuskategori: String? = "AAPEN"
    ): List<Oppgave> = tryWithOppgaveErrorHandling {
        dualOppgaveRestTemplate
            .get()
            .uri(hentOppgaverUriOptionalStatuskategori(journalpostId, oppgavetype, statuskategori))
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity<Oppgaver>()
            .body!!
            .oppgaver
    }

    fun finnOppgaver(
        fristFom: LocalDate,
        fristTom: LocalDate,
        tema: String,
        oppgavetype: String,
        statuskategori: String,
        behandlingstema: String?,
        behandlingstype: String?,
        limit: Int?,
        offset: Int?
    ): List<Oppgave> = tryWithOppgaveErrorHandling {
        dualOppgaveRestTemplate
            .get()
            .uri(
                finnOppgaverUriOptionalBehandlingstemaBehandlingstype(
                    fristFom = fristFom,
                    fristTom = fristTom,
                    tema = tema,
                    oppgavetype = oppgavetype,
                    statuskategori = statuskategori,
                    behandlingstema = behandlingstema,
                    behandlingstype = behandlingstype,
                    limit = limit,
                    offset = offset
                )
            )
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity<Oppgaver>()
            .body!!
            .oppgaver
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

    fun finnOppgaverUriOptionalBehandlingstemaBehandlingstype(
        fristFom: LocalDate,
        fristTom: LocalDate,
        tema: String,
        oppgavetype: String,
        statuskategori: String,
        behandlingstema: String?,
        behandlingstype: String?,
        limit: Int?,
        offset: Int?
    ): URI {
        val uriComponentsBuilder = UriComponentsBuilder
            .fromHttpUrl("${oppgaveUrl}/api/v1/oppgaver")
            .queryParam("fristFom", fristFom.toString())
            .queryParam("fristTom", fristTom.toString())
            .queryParam("tema", tema)
            .queryParam("oppgavetype", oppgavetype)
            .queryParam("statuskategori", statuskategori)
        behandlingstema?.let {
            uriComponentsBuilder
                .queryParam("behandlingstema", behandlingstema)
        }
        behandlingstype?.let {
            uriComponentsBuilder
                .queryParam("behandlingstype", behandlingstype)
        }
        limit?.let {
            uriComponentsBuilder
                .queryParam("limit", limit)
        }
        offset?.let {
            uriComponentsBuilder
                .queryParam("offset", offset)
        }
        return uriComponentsBuilder.build().toUri()
    }

    private inline fun <T> tryWithOppgaveErrorHandling(kall: () -> T): T =
        try {
            kall()
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == BAD_REQUEST) {
                val oppgaveUgyldigRequest = e.getResponseBodyAs(OppgaveUgyldigRequest::class.java)!!
                throw OppgaveUgyldigRequestException(oppgaveUgyldigRequest)
            } else {
                throw e
            }
        }
}
