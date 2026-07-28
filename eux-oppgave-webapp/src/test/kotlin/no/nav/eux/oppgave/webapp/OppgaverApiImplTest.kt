package no.nav.eux.oppgave.webapp

import io.kotest.assertions.json.shouldMatchJsonResource
import no.nav.eux.oppgave.openapi.model.FinnOppgaverResponsOpenApiType
import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType
import no.nav.eux.oppgave.webapp.common.*
import no.nav.eux.oppgave.webapp.dataset.*
import no.nav.eux.oppgave.webapp.model.TestModelBehandleSedFraJournalpostId
import no.nav.eux.oppgave.webapp.model.TestModelBehandleSedFraJournalpostIdMedAktoerId
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillRespons
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillingStatus.FERDIGSTILLING_FEILET
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillingStatus.OPPGAVE_FERDIGSTILT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

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
    fun `POST oppgaver - forespørsel, med uuid, duplikat - 409`() {
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
    fun `POST oppgaver - forespørsel, retry etter feilet uuid - 201`() {
        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseFeilmeldingMedUuid)
            .exchange()
            .expectStatus().isEqualTo(400)

        restTestClient
            .post()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverOpprettelseMedUuid)
            .exchange()
            .expectStatus().isEqualTo(201)
        requestBodies["/api/v1/oppgaver"]!! shouldMatchJsonResource "/dataset/expected/oppgave-opprett.json"
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
    fun `POST oppgaver tildelEnhetsnr - forespørsel, valid - 204`() {
        restTestClient
            .post()
            .uri(oppgaverTildelEnhetsnrUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverTildelEnhetsnrDataset)
            .exchange()
            .expectStatus().isEqualTo(204)
        val requestBody = requestBodies["/api/v1/oppgaver/190402"]!!
        val result = requestBody.jsonNode
        assertThat(result["versjon"].intValue()).isEqualTo(4)
        assertThat(result["tildeltEnhetsnr"].stringValue()).isEqualTo("2950")
        assertThat(result["tilordnetRessurs"].stringValue()).isNull()
        assertThat(result["kommentar"]["tekst"].stringValue()).isEqualTo("en kommentar")
        assertThat(result["kommentar"]["automatiskGenerert"].booleanValue()).isFalse()
        assertThat(result["beskrivelse"].stringValue()).contains("en kommentar")
        assertThat(result["beskrivelse"].stringValue()).contains("desc")
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
    fun `POST finn oppgaver - forespørsel, med behandlingstema - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${finnOppgaverDatasetBehandlingstema.fristFom}" +
                "&fristTom=${finnOppgaverDatasetBehandlingstema.fristTom}&tema=BAR" +
                "&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstema=ab0058&limit=200&offset=10"
        val finnOppgaverRespons = restTestClient
            .post()
            .uri(oppgaverFinnUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(finnOppgaverDatasetBehandlingstema)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(FinnOppgaverResponsOpenApiType::class.java)
            .returnResult()
        assertThat(requestBodies.containsKey(oppgaverFinnParameterUrl)).isTrue()
        assertThat(finnOppgaverRespons.responseBody!!.oppgaver!![0].id).isEqualTo(190402)
    }

    @Test
    fun `POST finn oppgaver - forespørsel, med behandlingstype - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${finnOppgaverDatasetBehandlingstype.fristFom}" +
                "&fristTom=${finnOppgaverDatasetBehandlingstype.fristTom}&tema=BAR" +
                "&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstype=ae0106&limit=200&offset=10"
        val finnOppgaverRespons = restTestClient
            .post()
            .uri(oppgaverFinnUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(finnOppgaverDatasetBehandlingstype)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(FinnOppgaverResponsOpenApiType::class.java)
            .returnResult()
        assertThat(requestBodies.containsKey(oppgaverFinnParameterUrl)).isTrue()
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

    @Test
    fun `POST oppgaver behandleSedFraJournalpostId - allerede eksisterende BEH_SED - 201`() {
        restTestClient
            .post()
            .uri(behandleSedFraJournalpostIdUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(TestModelBehandleSedFraJournalpostId("111111"))
            .exchange()
            .expectStatus().isEqualTo(201)
            .expectBody(OppgaveOpenApiType::class.java)
            .returnResult()
            .also {
                assertThat(it.responseBody!!.oppgavetype).isEqualTo("BEH_SED")
                assertThat(it.responseBody!!.id).isEqualTo(190403)
            }
        assertThat(requestBodies.containsKey("/api/v1/oppgaver")).isFalse()
    }

    @Test
    fun `POST oppgaver ferdigstill - ekstern oppgave ikke ferdigstilt - 200`() {
        val createResponse = restTestClient
            .post()
            .uri(oppgaverFerdigstillUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverFerdigstillFeiletDataset)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(TestModelFerdigstillRespons::class.java)
            .returnResult()
        assertThat(createResponse.responseBody!!.oppgaver[0].status)
            .isEqualTo(FERDIGSTILLING_FEILET)
        assertThat(createResponse.responseBody!!.oppgaver[0].beskrivelse)
            .isEqualTo("Kall mot oppgave feilet ikke, men oppgave ble ikke ferdigstilt")
    }

    @Test
    fun `POST oppgaver ferdigstill - flere oppgaver, en ekstern feil - 200`() {
        val createResponse = restTestClient
            .post()
            .uri(oppgaverFerdigstillUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppgaverFerdigstillDelvisFeiletDataset)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(TestModelFerdigstillRespons::class.java)
            .returnResult()
        with(createResponse.responseBody!!.oppgaver) {
            assertThat(this).hasSize(2)
            assertThat(this[0].status).isEqualTo(OPPGAVE_FERDIGSTILT)
            assertThat(this[1].status).isEqualTo(FERDIGSTILLING_FEILET)
            assertThat(this[1].oppgave!!.id).isEqualTo(999999)
            assertThat(this[1].beskrivelse)
                .isEqualTo("Ferdigstilling av oppgave 999999 feilet pga. manglende respons fra oppgave-systemet")
        }
        assertThat(requestBodies["/api/v1/oppgaver/999999"]!!.jsonNode).isNotEmpty
    }

    @Test
    fun `PATCH oppdater oppgave - ekstern feil, returnerer uendret oppgave - 200`() {
        val oppdaterOppgaveRespons = restTestClient
            .patch()
            .uri(oppgaverUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(oppdaterOppgaveFeiletDataset)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(OppgaveOpenApiType::class.java)
            .returnResult()
        assertThat(oppdaterOppgaveRespons.responseBody!!.id).isEqualTo(999999)
        assertThat(oppdaterOppgaveRespons.responseBody!!.versjon).isEqualTo(1)
    }

    @Test
    fun `POST finn oppgaver - forespørsel, uten paginering - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${finnOppgaverDatasetUtenPaginering.fristFom}" +
                "&fristTom=${finnOppgaverDatasetUtenPaginering.fristTom}&tema=BAR" +
                "&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstema=ab0058"
        val finnOppgaverRespons = restTestClient
            .post()
            .uri(oppgaverFinnUrl)
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .body(finnOppgaverDatasetUtenPaginering)
            .exchange()
            .expectStatus().isEqualTo(200)
            .expectBody(FinnOppgaverResponsOpenApiType::class.java)
            .returnResult()
        assertThat(requestBodies.containsKey(oppgaverFinnParameterUrl)).isTrue()
        assertThat(finnOppgaverRespons.responseBody!!.oppgaver!![0].id).isEqualTo(190402)
    }
}
