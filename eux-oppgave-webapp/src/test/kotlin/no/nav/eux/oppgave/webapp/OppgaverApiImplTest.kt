package no.nav.eux.oppgave.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.eux.oppgave.webapp.common.oppgaverFerdigstillUrl
import no.nav.eux.oppgave.webapp.common.oppgaverTildelEnhetsnrUrl
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.dataset.oppgaverFerdigstillDataset
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelse
import no.nav.eux.oppgave.webapp.dataset.oppgaverTildelEnhetsnrDataset
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillRespons
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillingStatus.OPPGAVE_FERDIGSTILT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

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
        val result = ObjectMapper().readTree(
            javaClass.getResource("/dataset/oppgaver-tildelt-enhetsnr.json")!!.readText()
        )
        assertThat(result["versjon"].intValue()).isEqualTo(4)
        assertThat(result["tildeltEnhetsnr"].textValue()).isEqualTo("2950")
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
}
