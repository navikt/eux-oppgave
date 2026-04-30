package no.nav.eux.oppgave.webapp

import tools.jackson.databind.ObjectMapper
import io.kotest.assertions.json.shouldMatchJsonResource
import no.nav.eux.oppgave.openapi.model.FinnOppgaverResponsOpenApiType
import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType
import no.nav.eux.oppgave.webapp.common.*
import no.nav.eux.oppgave.webapp.dataset.*
import no.nav.eux.oppgave.webapp.model.TestModelBehandleSedFraJournalpostId
import no.nav.eux.oppgave.webapp.model.TestModelBehandleSedFraJournalpostIdMedAktoerId
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillRespons
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillingStatus.OPPGAVE_FERDIGSTILT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDate

class OppgaverApiImplTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST oppgaver - forespørsel, valid - 201`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelse)
            .exchange()
            .expectStatus().isEqualTo(201)
        val request = requestBodies["/api/v1/oppgaver"]!!
        request shouldMatchJsonResource "/dataset/expected/oppgave-opprett.json"
    }

    @Test
    fun `POST oppgaver - forespørsel, duplikat journalpostId - 409`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelse)
            .exchange()
            .expectStatus().isEqualTo(201)
        val request = requestBodies["/api/v1/oppgaver"]!!
        request shouldMatchJsonResource "/dataset/expected/oppgave-opprett.json"

        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelse)
            .exchange()
            .expectStatus().isEqualTo(409)
    }

    @Test
    fun `POST oppgaver - forespørsel, valid, ikke lag nesten lik oppgave - 201`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseIkkeLagNestenLik)
            .exchange()
            .expectStatus().isEqualTo(201)
        val request = requestBodies["/api/v1/oppgaver"]!!
        request shouldMatchJsonResource "/dataset/expected/oppgave-opprett-nesten-lik.json"
    }

    @Test
    fun `POST oppgaver - forespørsel, valid, med uuid - 201`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseMedUuid)
            .exchange()
            .expectStatus().isEqualTo(201)
        val request = requestBodies["/api/v1/oppgaver"]!!
        request shouldMatchJsonResource "/dataset/expected/oppgave-opprett.json"
    }

    @Test
    fun `POST oppgaver - med uuid, conflict - 409`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseMedUuid)
            .exchange()
            .expectStatus().isEqualTo(201)
        val createResponse2 = restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseMedUuid)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(String::class.java)
            .returnResult()
        createResponse2.responseBody!! shouldMatchJsonResource "/dataset/expected/oppgave-opprett-conflict.json"
    }

    @Test
    fun `POST oppgaver behandleSedFraJournalpostId - forespørsel, valid - 201`() {
        restTestClient
            .post()
            .uri(behandleSedFraJournalpostIdUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(TestModelBehandleSedFraJournalpostId("453857122"))
            .exchange()
            .expectStatus().isEqualTo(201)
        val request = requestBodies["/api/v1/oppgaver"]!!
        request shouldMatchJsonResource "/dataset/expected/oppgave-opprett-behandleSedFraJournalpostId.json"
    }

    @Test
    fun `POST oppgaver behandleSedFraJournalpostId - forespørsel, med aktoerId, valid - 201`() {
        restTestClient
            .post()
            .uri(behandleSedFraJournalpostIdUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(TestModelBehandleSedFraJournalpostIdMedAktoerId("453857122", "2280720130426"))
            .exchange()
            .expectStatus().isEqualTo(201)
        val request = requestBodies["/api/v1/oppgaver"]!!
        request shouldMatchJsonResource
                "/dataset/expected/oppgave-opprett-behandleSedFraJournalpostIdMedPersonident.json"
    }

    @Test
    fun `POST oppgaver ferdigstill - forespørsel, valid - 200`() {
        val createResponse = restTestClient
            .post()
            .uri(oppgaverFerdigstillUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverFerdigstillDataset)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(TestModelFerdigstillRespons::class.java)
            .returnResult()
        val request = requestBodies["/api/v1/oppgaver/190402"]!!
        request shouldMatchJsonResource "/dataset/expected/oppgaver-ferdigstill.json"
        assertThat(createResponse.responseBody!!.oppgaver[0].status)
            .isEqualTo(OPPGAVE_FERDIGSTILT)
        assertThat(createResponse.responseBody!!.oppgaver[0].beskrivelse)
            .isEqualTo("Oppgave 190402 ble ferdigstilt")
    }

    @Test
    fun `POST oppgaver tildel enhetsnummer - forespørsel, valid - 204`() {
        restTestClient
            .post()
            .uri(oppgaverTildelEnhetsnrUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverTildelEnhetsnrDataset)
            .exchange()
            .expectStatus().isEqualTo(204)
        val requestBody = requestBodies["/api/v1/oppgaver/190402"]!!
        println(requestBody)
        assertThat(requestBody).contains("tilordnetRessurs")
        assertThat(requestBody).contains("kommentar")
        assertThat(requestBody).contains("beskrivelse")
        val result = ObjectMapper().readTree(requestBody)
        assertThat(result["versjon"].intValue()).isEqualTo(4)
        assertThat(result["tildeltEnhetsnr"].textValue()).isEqualTo("2950")
        assertThat(result["tilordnetRessurs"].textValue()).isNull()
    }

    @Test
    fun `POST oppgaver - ikke autentisert - 401`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body("{}")
            .exchange()
            .expectStatus().isEqualTo(401)
    }

    @Test
    fun `POST oppgaver - ugyldig request - 400`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(".")
            .exchange()
            .expectStatus().isEqualTo(400)
    }

    @Test
    fun `POST finn oppgaver med behandlingstema - forespørsel, valid - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${LocalDate.now()}fristFom&fristTom=${LocalDate.now()}fristTom&tema=BAR" +
                "&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstema=ab0058"
        val finnOppgaverRespons = restTestClient
            .post()
            .uri(oppgaverFinnUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(finnOppgaverDatasetBehandlingstema)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(FinnOppgaverResponsOpenApiType::class.java)
            .returnResult()
        assertThat(requestBodies.containsKey(oppgaverFinnParameterUrl))
        assertThat(finnOppgaverRespons.responseBody!!.oppgaver!![0].id).isEqualTo(190402)
    }

    @Test
    fun `POST finn oppgaver med behandlingstype - forespørsel, valid - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${LocalDate.now()}fristFom&fristTom=${LocalDate.now()}fristTom&tema=BAR" +
                "&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstype=ae0106&limit=200&offset=10"
        val finnOppgaverRespons = restTestClient
            .post()
            .uri(oppgaverFinnUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(finnOppgaverDatasetBehandlingstema)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(FinnOppgaverResponsOpenApiType::class.java)
            .returnResult()
        assertThat(requestBodies.containsKey(oppgaverFinnParameterUrl))
        assertThat(finnOppgaverRespons.responseBody!!.oppgaver!![0].id).isEqualTo(190402)
    }

    @Test
    fun `PATCH oppdater oppgave - forespørsel, valid - 200`() {
        val oppdaterOppgaveRespons = restTestClient
            .patch()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppdaterOppgaveDataset)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(OppgaveOpenApiType::class.java)
            .returnResult()
        assertThat(requestBodies["$oppgaverUrl/190402"]!!.jsonNode).isNotEmpty
        assertThat(oppdaterOppgaveRespons.responseBody!!.versjon).isEqualTo(4)
    }
}
