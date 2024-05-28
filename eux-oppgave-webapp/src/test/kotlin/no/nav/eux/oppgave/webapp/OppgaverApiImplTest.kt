package no.nav.eux.oppgave.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.eux.oppgave.openapi.model.FinnOppgaverResponsOpenApiType
import no.nav.eux.oppgave.openapi.model.OppgaveOpenApiType
import no.nav.eux.oppgave.webapp.common.*
import no.nav.eux.oppgave.webapp.dataset.*
import no.nav.eux.oppgave.webapp.model.TestModelBehandleSedFraJournalpostId
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillRespons
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillingStatus.OPPGAVE_FERDIGSTILT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.LocalDate

class OppgaverApiImplTest : AbstractOppgaverApiImplTest() {

    @Test
    fun `POST oppgaver - forespørsel, valid - 201`() {
        val createResponse = restTemplate
            .postForEntity<String>(
                oppgaverUrl,
                oppgaverOpprettelse.httpEntity
            )
        assertThat(
            requestBodies["/api/v1/oppgaver"]!!.jsonNode
        )
            .isEqualTo(
                ObjectMapper().readTree(
                    javaClass.getResource("/dataset/oppgave-opprett.json")!!.readText()
                )
            )
        assertThat(createResponse.statusCode.value()).isEqualTo(201)
    }

    @Test
    fun `POST oppgaver behandleSedFraJournalpostId - forespørsel, valid - 201`() {
        val createResponse = restTemplate
            .postForEntity<String>(
                behandleSedFraJournalpostIdUrl,
                TestModelBehandleSedFraJournalpostId("453857122").httpEntity
            )
        assertThat(
            requestBodies["/api/v1/oppgaver"]!!.jsonNode
        )
            .isEqualTo(
                ObjectMapper().readTree(
                    javaClass
                        .getResource("/dataset/oppgave-opprett-behandleSedFraJournalpostId.json")!!
                        .readText()
                )
            )
        assertThat(createResponse.statusCode.value()).isEqualTo(201)
    }

    @Test
    fun `POST oppgaver ferdigstill - forespørsel, valid - 200`() {
        val createResponse = restTemplate
            .postForEntity<TestModelFerdigstillRespons>(
                oppgaverFerdigstillUrl,
                oppgaverFerdigstillDataset.httpEntity
            )
        assertThat(
            requestBodies["/api/v1/oppgaver/190402"]!!.jsonNode
        )
            .isEqualTo(
                ObjectMapper().readTree(
                    javaClass.getResource("/dataset/oppgaver-ferdigstill.json")!!.readText()
                )
            )
        assertThat(createResponse.statusCode.value()).isEqualTo(200)
        assertThat(createResponse.body!!.oppgaver[0].status)
            .isEqualTo(OPPGAVE_FERDIGSTILT)
        assertThat(createResponse.body!!.oppgaver[0].beskrivelse)
            .isEqualTo("Oppgave 190402 ble ferdigstilt")
    }

    @Test
    fun `POST oppgaver tildel enhetsnummer - forespørsel, valid - 204`() {
        val createResponse = restTemplate
            .postForEntity<Void>(
                oppgaverTildelEnhetsnrUrl,
                oppgaverTildelEnhetsnrDataset.httpEntity
            )
        val requestBody = requestBodies["/api/v1/oppgaver/190402"]!!
        println(requestBody)
        assertThat(requestBody).contains("tilordnetRessurs")
        assertThat(requestBody).contains("kommentar")
        assertThat(requestBody).contains("beskrivelse")
        val result = ObjectMapper().readTree(requestBody)
        assertThat(result["versjon"].intValue()).isEqualTo(4)
        assertThat(result["tildeltEnhetsnr"].textValue()).isEqualTo("2950")
        assertThat(result["tilordnetRessurs"].textValue()).isNull()
        assertThat(createResponse.statusCode.value()).isEqualTo(204)
    }

    @Test
    fun `POST oppgaver - ikke autentisert - 401`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity<String>("{}", headers)
        val createResponse = restTemplate.postForEntity<Void>(
            oppgaverUrl,
            entity
        )
        assertThat(createResponse.statusCode.value()).isEqualTo(401)
    }

    @Test
    fun `POST oppgaver - ugyldig request - 400`() {
        val createResponse = restTemplate.postForEntity<Void>(
            oppgaverUrl,
            ".".httpEntity
        )
        assertThat(createResponse.statusCode.value()).isEqualTo(400)
    }

    @Test
    fun `POST finn oppgaver med behandlingstema - forespørsel, valid - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${LocalDate.now()}fristFom&fristTom=${LocalDate.now()}fristTom&tema=BAR&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstema=ab0058"


        val finnOppgaverRespons = restTemplate
            .postForEntity<FinnOppgaverResponsOpenApiType>(
                oppgaverFinnUrl,
                finnOppgaverDatasetBehandlingstema.httpEntity
            )
        assertThat(
            requestBodies.containsKey(oppgaverFinnParameterUrl)
        )
        assertThat(finnOppgaverRespons.statusCode.value()).isEqualTo(200)

        assertThat(finnOppgaverRespons.body!!.oppgaver!!.get(0).id).isEqualTo(190402)

    }

    @Test
    fun `POST finn oppgaver med behandlingstype - forespørsel, valid - 200`() {
        val oppgaverFinnParameterUrl = "/api/v1/oppgaver" +
                "?fristFom=${LocalDate.now()}fristFom&fristTom=${LocalDate.now()}fristTom&tema=BAR&oppgavetype=FREM&statuskategori=AAPEN" +
                "&behandlingstype=ae0106"

        val finnOppgaverRespons = restTemplate
            .postForEntity<FinnOppgaverResponsOpenApiType>(
                oppgaverFinnUrl,
                finnOppgaverDatasetBehandlingstema.httpEntity
            )
        assertThat(
            requestBodies.containsKey(oppgaverFinnParameterUrl)
        )
        assertThat(finnOppgaverRespons.statusCode.value()).isEqualTo(200)

        assertThat(finnOppgaverRespons.body!!.oppgaver!!.get(0).id).isEqualTo(190402)
    }

    @Test
    fun `PATCH oppdater oppgave - forespørsel, valid - 200`() {

        val oppdaterOppgaveRespons = restTemplate
            .patchForObject<OppgaveOpenApiType>(
                oppgaverUrl,
                oppdaterOppgaveDataset.httpEntity,
                OppgaveOpenApiType::class.java
            )
        assertThat(
            requestBodies[oppgaverUrl + "/190402"]!!.jsonNode
        ).isNotEmpty

        assertThat(oppdaterOppgaveRespons.versjon).isEqualTo(4)
    }
}
