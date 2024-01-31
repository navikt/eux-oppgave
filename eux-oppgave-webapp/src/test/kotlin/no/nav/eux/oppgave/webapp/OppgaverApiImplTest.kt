package no.nav.eux.oppgave.webapp

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.eux.oppgave.webapp.common.oppgaverFerdigstillUrl
import no.nav.eux.oppgave.webapp.common.oppgaverUrl
import no.nav.eux.oppgave.webapp.dataset.oppgaverFerdigstillDataset
import no.nav.eux.oppgave.webapp.dataset.oppgaverOpprettelse
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillRespons
import no.nav.eux.oppgave.webapp.model.TestModelFerdigstillingStatus.OPPGAVE_FERDIGSTILT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.postForEntity

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
                    javaClass.getResource("/oppgave-opprett.json")!!.readText()
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
                    javaClass.getResource("/oppgaver-ferdigstill.json")!!.readText()
                )
            )
        assertThat(createResponse.statusCode.value()).isEqualTo(200)
        assertThat(createResponse.body!!.oppgaver[0].status)
            .isEqualTo(OPPGAVE_FERDIGSTILT)
        assertThat(createResponse.body!!.oppgaver[0].beskrivelse)
            .isEqualTo("Oppgave 190402 ble ferdigstilt")
    }

    val String.jsonNode: JsonNode get() = ObjectMapper().readTree(this)

}
